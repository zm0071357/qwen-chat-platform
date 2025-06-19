package qwen.chat.platform.api;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import qwen.chat.platform.api.dto.ChatRequestDTO;
import qwen.chat.platform.api.dto.HistoryRequestDTO;
import qwen.chat.platform.api.dto.UploadFileResponseDTO;
import qwen.chat.platform.api.response.Response;
import qwen.sdk.largemodel.chat.model.ChatRequest;

import java.util.List;

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

    /**
     * 获取聊天记录码
     * @param userId
     * @return
     */
    Response<List<String>> getHistoryCodeList(String userId);

    /**
     * 根据用户ID和聊天记录码获取聊天记录
     * @param historyRequestDTO
     * @return
     */
    Response<List<ChatRequest.Input.Message>> getHistory(HistoryRequestDTO historyRequestDTO);
}
