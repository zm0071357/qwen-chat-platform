package qwen.chat.platform.api;

import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import qwen.chat.platform.api.dto.ChatRequestDTO;

public interface ChatService {

    /**
     * 对话
     * @param chatRequestDTO
     * @return
     */
    ResponseBodyEmitter chat(ChatRequestDTO chatRequestDTO);
}
