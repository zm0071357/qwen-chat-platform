package qwen.chat.platform.domain.qwen.adapter.repository;

import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import qwen.sdk.largemodel.chat.model.ChatRequest;

import java.util.List;

public interface QwenRepository {
    
    List<ChatRequest.Input.Message> getHistory(String userId, String historyCode);

    ResponseBodyEmitter chat(List<ChatRequest.Input.Message> messages, String content, boolean search, String historyCode, String userId);

    ResponseBodyEmitter chatWithLink(List<ChatRequest.Input.Message> messages, String content, boolean search, String historyCode, String userId);
}
