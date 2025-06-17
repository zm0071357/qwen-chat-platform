package qwen.chat.platform.api;

import qwen.chat.platform.api.dto.ImageRequestDTO;
import qwen.chat.platform.api.dto.ImageResponseDTO;
import qwen.chat.platform.api.response.Response;

public interface ImageService {

    Response<ImageResponseDTO> createImage(ImageRequestDTO imageRequestDTO);

}
