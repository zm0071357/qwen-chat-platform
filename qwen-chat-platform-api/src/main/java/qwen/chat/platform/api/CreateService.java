package qwen.chat.platform.api;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import qwen.chat.platform.api.dto.ImageRequestDTO;
import qwen.chat.platform.api.dto.CreateResponseDTO;
import qwen.chat.platform.api.dto.UploadFileResponseDTO;
import qwen.chat.platform.api.dto.VideoRequestDTO;
import qwen.chat.platform.api.response.Response;

public interface CreateService {

    Response<CreateResponseDTO> createImage(ImageRequestDTO imageRequestDTO);

    Response<CreateResponseDTO> createVideo(VideoRequestDTO videoRequestDTO);

    Response<UploadFileResponseDTO> uploadImage(MultipartFile file, String userId);
}
