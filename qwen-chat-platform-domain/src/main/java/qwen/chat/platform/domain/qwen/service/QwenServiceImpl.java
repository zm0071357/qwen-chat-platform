package qwen.chat.platform.domain.qwen.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import qwen.chat.platform.domain.qwen.DefaultQwenService;
import qwen.chat.platform.domain.qwen.adapter.repository.QwenRepository;
import qwen.chat.platform.domain.qwen.model.entity.MessageEntity;
import qwen.chat.platform.domain.qwen.model.valobj.RoleConstant;
import qwen.sdk.largemodel.chat.model.ChatRequest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class QwenServiceImpl extends DefaultQwenService {

    @Resource
    private QwenRepository qwenRepository;

    @Override
    protected ResponseBodyEmitter handleFileMessage(MessageEntity messageEntity) {
        return null;
    }

    @Override
    protected ResponseBodyEmitter handleTextMessage(MessageEntity messageEntity) {
        String content = messageEntity.getContent();
        String historyCode = messageEntity.getHistoryCode();
        String userId = messageEntity.getUserId();
        boolean search = messageEntity.isSearch();
        // 获取历史记录
        List<ChatRequest.Input.Message> messages = qwenRepository.getHistory(userId, historyCode);
        // 视频链接
        if (messageEntity.isLink(content)) {
            return qwenRepository.chatWithLink(messages, content, search, historyCode, userId);
        }
        // 普通文本
        return qwenRepository.chat(messages, content, search, historyCode, userId);
    }


}
