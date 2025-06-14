package qwen.chat.platform.domain.qwen;

import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import qwen.chat.platform.domain.qwen.model.entity.MessageEntity;

public interface QwenService {
    ResponseBodyEmitter handle(MessageEntity messageEntity);
}
