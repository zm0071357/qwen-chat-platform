package qwen.chat.platform.api;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import qwen.chat.platform.api.dto.ChatRequestDTO;
import qwen.chat.platform.api.dto.UploadFileResponseDTO;
import qwen.chat.platform.api.response.Response;

public interface ChatService {

    /**
     * 对话
     * @param chatRequestDTO
     * @return
     */
    ResponseBodyEmitter chat(ChatRequestDTO chatRequestDTO);

    /**
     * 上传文件
     * @param file
     * @param userId
     * @return
     */
    Response<UploadFileResponseDTO> uploadFile(MultipartFile file, String userId);
}
