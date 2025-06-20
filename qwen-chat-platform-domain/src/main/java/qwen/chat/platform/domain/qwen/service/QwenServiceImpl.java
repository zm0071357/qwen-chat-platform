package qwen.chat.platform.domain.qwen.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import qwen.chat.platform.domain.qwen.DefaultQwenService;
import qwen.chat.platform.domain.qwen.adapter.repository.QwenRepository;
import qwen.chat.platform.domain.qwen.model.entity.MessageEntity;
import qwen.chat.platform.domain.qwen.model.entity.PreRequestEntity;
import qwen.sdk.largemodel.chat.model.ChatRequest;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class QwenServiceImpl extends DefaultQwenService {

    @Resource
    private QwenRepository qwenRepository;

    @Override
    protected ResponseBodyEmitter handleFileMessage(MessageEntity messageEntity) {
        String userId = messageEntity.getUserId();
        String historyCode = messageEntity.getHistoryCode();
        // 历史记录
        List<ChatRequest.Input.Message> historyMessages = this.getHistory(userId, historyCode, true);
        // 请求记录
        List<ChatRequest.Input.Message> requestHistoryMessages = this.getHistory(userId, historyCode, false);
        PreRequestEntity preRequestEntity = PreRequestEntity.builder()
                .userId(userId)
                .historyCode(historyCode)
                .content(messageEntity.getContent())
                .think(messageEntity.isThink())
                .search(messageEntity.isSearch())
                .fileList(messageEntity.getFileList())
                .build();
        return qwenRepository.chatWithFile(historyMessages, requestHistoryMessages, preRequestEntity);
    }

    @Override
    protected ResponseBodyEmitter handleTextMessage(MessageEntity messageEntity) {
        String userId = messageEntity.getUserId();
        String historyCode = messageEntity.getHistoryCode();
        // 历史记录
        List<ChatRequest.Input.Message> historyMessages = this.getHistory(userId, historyCode, true);
        // 请求记录
        List<ChatRequest.Input.Message> requestHistoryMessages = this.getHistory(userId, historyCode, false);
        PreRequestEntity preRequestEntity = PreRequestEntity.builder()
                .userId(userId)
                .historyCode(historyCode)
                .content(messageEntity.getContent())
                .think(messageEntity.isThink())
                .search(messageEntity.isSearch())
                .build();
        if (!preRequestEntity.isLink(preRequestEntity.getContent())) {
            return qwenRepository.chat(historyMessages, requestHistoryMessages, preRequestEntity);
        }
        return qwenRepository.chatWithLink(historyMessages, requestHistoryMessages, preRequestEntity);
    }

    @Override
    public List<ChatRequest.Input.Message> getHistory(String userId, String historyCode, boolean isHistory) {
        return qwenRepository.getHistory(userId, historyCode, isHistory);
    }

    @Override
    public List<String> getHistoryCodeList(String userId) {
        return qwenRepository.getHistoryCodeList(userId);
    }
}
