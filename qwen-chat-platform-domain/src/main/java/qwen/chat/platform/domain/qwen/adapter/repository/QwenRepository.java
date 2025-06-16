package qwen.chat.platform.domain.qwen.adapter.repository;

import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import qwen.chat.platform.domain.qwen.model.entity.MessageEntity;
import qwen.chat.platform.domain.qwen.model.valobj.File;
import qwen.sdk.largemodel.chat.model.ChatRequest;

import java.util.List;

public interface QwenRepository {
    
    List<ChatRequest.Input.Message> getHistory(String userId, String historyCode);

    ResponseBodyEmitter chat(List<ChatRequest.Input.Message> messages, String content, boolean search, String historyCode, String userId);

    ResponseBodyEmitter chatWithLink(List<ChatRequest.Input.Message> messages, String content, boolean search, String historyCode, String userId);

    ResponseBodyEmitter chatWithFile(List<ChatRequest.Input.Message> messages, MessageEntity messageEntity);
}
