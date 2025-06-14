package qwen.chat.platform.infrastructure.adapter.repository;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import qwen.chat.platform.domain.qwen.adapter.port.OnlineLinkPort;
import qwen.chat.platform.domain.qwen.adapter.repository.QwenRepository;
import qwen.chat.platform.domain.qwen.model.entity.AnalysisVideoEntity;
import qwen.chat.platform.domain.qwen.model.valobj.MessageConstant;
import qwen.chat.platform.domain.qwen.model.valobj.RoleConstant;
import qwen.chat.platform.infrastructure.dao.QwenDao;
import qwen.chat.platform.infrastructure.dao.po.History;
import qwen.sdk.largemodel.chat.enums.ChatModelEnum;
import qwen.sdk.largemodel.chat.impl.ChatServiceImpl;
import qwen.sdk.largemodel.chat.model.ChatMutiResponse;
import qwen.sdk.largemodel.chat.model.ChatMutiStreamResponse;
import qwen.sdk.largemodel.chat.model.ChatRequest;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class QwenRepositoryImpl implements QwenRepository {

    @Resource
    private QwenDao qwenDao;

    private final OnlineLinkPort onlineLinkPort;
    private final ChatServiceImpl chatServiceImpl;

    public QwenRepositoryImpl(OnlineLinkPort onlineLinkPort, ChatServiceImpl chatServiceImpl) {
        this.onlineLinkPort = onlineLinkPort;
        this.chatServiceImpl = chatServiceImpl;
    }

    @Override
    public List<ChatRequest.Input.Message> getHistory(String userId, String historyCode) {
        History history = qwenDao.getHistory(userId, historyCode);
        if (history == null) {
            List<ChatRequest.Input.Message> messages = new ArrayList<>();
            List<ChatRequest.Input.Message.Content> systemContent = new ArrayList<>();
            systemContent.add(ChatRequest.Input.Message.Content.builder().text(MessageConstant.DEFAULT_MESSAGE).build());
            messages.add(ChatRequest.Input.Message.builder()
                    .role(RoleConstant.SYSTEM)
                    .content(systemContent)
                    .build());
            qwenDao.insert(History.builder()
                    .userId(userId)
                    .historyCode(historyCode)
                    .historyJson(JSON.toJSONString(messages))
                    .build());
            return messages;
        }
        return JSON.parseArray(history.getHistoryJson(), ChatRequest.Input.Message.class);
    }

    @Override
    public ResponseBodyEmitter chat(List<ChatRequest.Input.Message> messages, String content, boolean search, String historyCode, String userId) {
        // 添加历史记录
        List<ChatRequest.Input.Message.Content> userContent = new ArrayList<>();
        userContent.add(ChatRequest.Input.Message.Content.builder()
                .text(content)
                .build());
        // 构造参数
        messages.add(ChatRequest.Input.Message.builder()
                .role(RoleConstant.USER)
                .content(userContent)
                .build());
        return this.handle(messages, search, historyCode, userId, true);
    }

    @Override
    public ResponseBodyEmitter chatWithLink(List<ChatRequest.Input.Message> messages, String content, boolean search, String historyCode, String userId) {
        String onlineLink = getOnlineLink(content);
        // 添加历史记录
        List<ChatRequest.Input.Message.Content> userContent = new ArrayList<>();
        userContent.add(ChatRequest.Input.Message.Content.builder()
                .video(onlineLink)
                .build());
        userContent.add(ChatRequest.Input.Message.Content.builder()
                .text(MessageConstant.DES_VIDEO_MESSAGE)
                .build());
        // 构造参数
        messages.add(ChatRequest.Input.Message.builder()
                .role(RoleConstant.USER)
                .content(userContent)
                .build());
        return this.handle(messages, search, historyCode, userId, false);
    }

    /**
     * 获取在线视频链接
     * @param link
     * @return
     */
    private String getOnlineLink(String link) {
        try {
            Call<AnalysisVideoEntity> call = onlineLinkPort.analysis(link);
            Response<AnalysisVideoEntity> response = call.execute();
            AnalysisVideoEntity analysisVideoEntity = response.body();
            return analysisVideoEntity.getData().getUrl();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 通用处理
     * @param messages
     * @param search
     * @param historyCode
     * @param userId
     * @return
     */
    private ResponseBodyEmitter handle(List<ChatRequest.Input.Message> messages, boolean search, String historyCode, String userId, boolean isSaveHistory) {
        StringBuilder result = new StringBuilder();
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(10 * 60 * 1000L);
        AtomicBoolean streamFailed = new AtomicBoolean(false); // 标记流是否失败
        try {
            // 构造参数
            ChatRequest request = ChatRequest.builder()
                    .model(ChatModelEnum.QWEN_VL_MAX_LATEST.getModel())
                    .input(ChatRequest.Input.builder()
                            .messages(messages)
                            .build())
                    .parameters(ChatRequest.Parameters.builder()
                            .resultFormat("message")
                            .enableSearch(search)
                            .incrementalOutput(true)
                            .searchOptions(search ? ChatRequest.Parameters.SearchOptions.builder()
                                    .enableSource(true)
                                    .forcedSearch(true)
                                    .build() : null)
                            .build())
                    .build();
            log.info("请求参数:{}", JSON.toJSONString(request));
            // 流式输出
            chatServiceImpl.chatWithMultimodalWithStream(request, new EventSourceListener() {
                @Override
                public void onEvent(EventSource eventSource, String id, String type, String data) {
                    try {
                        ChatMutiStreamResponse response = JSON.parseObject(data, ChatMutiStreamResponse.class);
                        log.info("response:{}", JSON.toJSONString(response));
                        if (response.getOutput() != null && response.getOutput().getChoices().get(0).getFinish_reason().equals("null")) {
                            // 获取文本内容
                            String text = response.getOutput().getChoices().get(0).getMessage().getContent().get(0).getText();
                            result.append(text);
                            emitter.send(text); // 实时发送到客户端
                        } else {
                            log.warn("收到的响应没有有效的输出或选项，数据: {}", data);
                        }
                    } catch (Exception e) {
                        streamFailed.set(true); // 标记流失败
                        emitter.completeWithError(new RuntimeException("流式数据处理失败", e));
                    }
                }

                @Override
                public void onClosed(EventSource eventSource) {
                    if (!streamFailed.get()) {
                        try {
                            log.info("result:{}", result);
                            if (isSaveHistory) {
                                // 将回答加入历史记录
                                List<ChatRequest.Input.Message.Content> systemContent = new ArrayList<>();
                                systemContent.add(ChatRequest.Input.Message.Content.builder()
                                        .text(String.valueOf(result))
                                        .build());
                                messages.add(ChatRequest.Input.Message.builder()
                                        .role(RoleConstant.SYSTEM)
                                        .content(systemContent)
                                        .build());
                                // 构造参数
                                History history = History.builder()
                                        .userId(userId)
                                        .historyCode(historyCode)
                                        .historyJson(JSON.toJSONString(messages))
                                        .build();
                                // 保存至数据库
                                log.info("保存至数据库");
                                qwenDao.update(history);
                            }
                            emitter.complete();
                        } catch (Exception e) {
                            emitter.completeWithError(new RuntimeException("保存历史记录失败", e));
                        }
                    }
                }

                @Override
                public void onFailure(EventSource eventSource, Throwable t, okhttp3.Response response) {
                    streamFailed.set(true); // 标记流失败
                    String errorMsg = "流式请求失败: " + (response != null ? response.message() : "未知错误");
                    log.error(errorMsg, t);
                    emitter.completeWithError(new RuntimeException(errorMsg, t));
                }
            });
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }

}
