package qwen.chat.platform.infrastructure.adapter.repository;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import qwen.chat.platform.domain.qwen.adapter.port.OnlineLinkPort;
import qwen.chat.platform.domain.qwen.adapter.repository.QwenRepository;
import qwen.chat.platform.domain.qwen.model.MessageContentEntity;
import qwen.chat.platform.domain.qwen.model.entity.AnalysisVideoEntity;
import qwen.chat.platform.domain.qwen.model.entity.MessageEntity;
import qwen.chat.platform.domain.qwen.model.valobj.File;
import qwen.chat.platform.domain.qwen.model.valobj.FileTypeEnum;
import qwen.chat.platform.domain.qwen.model.valobj.MessageConstant;
import qwen.chat.platform.domain.qwen.model.valobj.RoleConstant;
import qwen.chat.platform.infrastructure.dao.QwenDao;
import qwen.chat.platform.infrastructure.dao.po.History;
import qwen.chat.platform.types.utils.AliOSSUtils;
import qwen.sdk.largemodel.chat.enums.ChatModelEnum;
import qwen.sdk.largemodel.chat.impl.ChatServiceImpl;
import qwen.sdk.largemodel.chat.model.ChatMutiResponse;
import qwen.sdk.largemodel.chat.model.ChatMutiStreamResponse;
import qwen.sdk.largemodel.chat.model.ChatRequest;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

@Slf4j
public class QwenRepositoryImpl implements QwenRepository {

    @Resource
    private QwenDao qwenDao;

    private final OnlineLinkPort onlineLinkPort;
    private final ChatServiceImpl chatServiceImpl;

    private final Map<FileTypeEnum, Function<MessageContentEntity, List<ChatRequest.Input.Message.Content>>> fileMap = new HashMap<>();

    public QwenRepositoryImpl(OnlineLinkPort onlineLinkPort, ChatServiceImpl chatServiceImpl) {
        this.onlineLinkPort = onlineLinkPort;
        this.chatServiceImpl = chatServiceImpl;
    }

    @PostConstruct
    private void init() {
        // 初始化
        fileMap.put(FileTypeEnum.IMAGE, entity -> Collections.singletonList(createImageMessage(entity.getFile())));
        fileMap.put(FileTypeEnum.AUDIO, entity -> Collections.singletonList(createAudioMessage(entity.getFile())));
        fileMap.put(FileTypeEnum.VIDEO, entity -> Collections.singletonList(createVideoMessage(entity.getFile())));
    }

    private ChatRequest.Input.Message.Content createAudioMessage(String file) {
        return ChatRequest.Input.Message.Content.builder()
                .audio(file)
                .build();
    }

    private ChatRequest.Input.Message.Content createVideoMessage(String file) {
        return ChatRequest.Input.Message.Content.builder()
                .video(file)
                .build();
    }

    private ChatRequest.Input.Message.Content createImageMessage(String file) {
        return ChatRequest.Input.Message.Content.builder()
                .image(file)
                .build();
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

    @Override
    public ResponseBodyEmitter chatWithFile(List<ChatRequest.Input.Message> messages, MessageEntity messageEntity) {
        List<String> fileList = messageEntity.getFileList();
        boolean isSaveHistory = true;
        Set<FileTypeEnum> mediaTypes = new HashSet<>();
        for (String file : messageEntity.getFileList()) {
            FileTypeEnum fileType = messageEntity.fileType(file);
            if (fileType == FileTypeEnum.IMAGE || fileType == FileTypeEnum.AUDIO || fileType == FileTypeEnum.VIDEO) {
                mediaTypes.add(fileType);
            }
        }
        if (mediaTypes.size() > 1) {
            log.info("文件类型超过2种");
            return this.handleWrong();
        }
        // 获取
        List<ChatRequest.Input.Message.Content> userContent = new ArrayList<>();
        for (String file : fileList) {
            FileTypeEnum fileTypeEnum = messageEntity.fileType(file);
            if (fileTypeEnum == FileTypeEnum.VIDEO || fileTypeEnum == FileTypeEnum.UNKNOWN) {
                isSaveHistory = false;
            }

            Function<MessageContentEntity, List<ChatRequest.Input.Message.Content>> handler = fileMap.get(fileTypeEnum);
            userContent.addAll(handler.apply(MessageContentEntity.builder()
                    .file(file)
                    .content(messageEntity.getContent())
                    .build()));
        }
        userContent.add(ChatRequest.Input.Message.Content.builder()
                .text(messageEntity.getContent() != null ? messageEntity.getContent() : MessageConstant.DES_FILE_MESSAGE)
                .build());
        // 构造参数
        messages.add(ChatRequest.Input.Message.builder()
                .role(RoleConstant.USER)
                .content(userContent)
                .build());
        return this.handle(messages, messageEntity.isSearch(), messageEntity.getHistoryCode(), messageEntity.getUserId(), isSaveHistory);
    }

    /**
     * 获取在线视频链接
     *
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
        AtomicBoolean resultFailed = new AtomicBoolean(false); // 标记回复是否失败
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
                            emitter.send(text);
                        } else if (response.getOutput() != null && response.getOutput().getChoices().get(0).getFinish_reason().equals("stop")) {
                            log.info("回答终止: {}", data);
                        } else {
                            log.warn("回答失败: {}", data);
                            messages.remove(messages.size() - 1);
                            resultFailed.set(true); // 标记回答失败
                            emitter.send(MessageConstant.TEXT_FAILED_MESSAGE);
                        }
                    } catch (Exception e) {
                        streamFailed.set(true); // 标记流失败
                        resultFailed.set(true); // 标记回答失败
                        emitter.completeWithError(new RuntimeException("流式数据处理失败", e));
                    }
                }

                @Override
                public void onClosed(EventSource eventSource) {
                    if (!streamFailed.get()) {
                        try {
                            log.info("result:{}", result);
                            if (isSaveHistory && !resultFailed.get()) {
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
                    messages.remove(messages.size() - 1);
                }
            });
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }


    private ResponseBodyEmitter handleWrong() {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(10 * 60 * 1000L);
        try {
            emitter.send(MessageConstant.VARIOUS_FILES_MESSAGE);
            emitter.complete();
            return emitter;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}