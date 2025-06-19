package qwen.chat.platform.domain.qwen;

import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import qwen.chat.platform.domain.qwen.model.entity.MessageEntity;
import qwen.sdk.largemodel.chat.model.ChatRequest;

import java.util.List;

public interface QwenService {
    ResponseBodyEmitter handle(MessageEntity messageEntity);

    List<ChatRequest.Input.Message> getHistory(String userId, String historyCode);

    List<String> getHistoryCodeList(String userId);
}
