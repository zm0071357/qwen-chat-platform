package qwen.chat.platform.domain.qwen.adapter.repository;

import qwen.chat.platform.domain.qwen.model.entity.CreateImageEntity;
import qwen.chat.platform.domain.qwen.model.entity.CreateVideoEntity;
import qwen.chat.platform.domain.qwen.model.entity.ResponseEntity;

public interface QwenCreateRepository {
    ResponseEntity createImage(CreateImageEntity createImageEntity);

    ResponseEntity descriptionEdit(CreateImageEntity createImageEntity);

    ResponseEntity removeWatermark(CreateImageEntity createImageEntity);

    ResponseEntity expand(CreateImageEntity createImageEntity);

    ResponseEntity superResolution(CreateImageEntity createImageEntity);

    ResponseEntity colorization(CreateImageEntity createImageEntity);

    ResponseEntity createVideo(CreateVideoEntity createVideoEntity);
}
