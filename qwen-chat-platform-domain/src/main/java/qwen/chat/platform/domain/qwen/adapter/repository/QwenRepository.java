package qwen.chat.platform.domain.qwen.adapter.repository;

import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import qwen.chat.platform.domain.qwen.model.entity.MessageEntity;
import qwen.chat.platform.domain.qwen.model.entity.PreRequestEntity;
import qwen.sdk.largemodel.chat.model.ChatRequest;

import java.util.List;

public interface QwenRepository {
    
    List<ChatRequest.Input.Message> getHistory(String userId, String historyCode, boolean isHistory);

    ResponseBodyEmitter chat(List<ChatRequest.Input.Message> historyMessages,
                             List<ChatRequest.Input.Message> requestHistoryMessages,
                             PreRequestEntity preRequestEntity);

    ResponseBodyEmitter chatWithLink(List<ChatRequest.Input.Message> historyMessages,
                                     List<ChatRequest.Input.Message> requestHistoryMessages,
                                     PreRequestEntity preRequestEntity);
    ResponseBodyEmitter chatWithFile(List<ChatRequest.Input.Message> historyMessages,
                                     List<ChatRequest.Input.Message> requestHistoryMessages,
                                     PreRequestEntity preRequestEntity);

    List<String> getHistoryCodeList(String userId);

}
