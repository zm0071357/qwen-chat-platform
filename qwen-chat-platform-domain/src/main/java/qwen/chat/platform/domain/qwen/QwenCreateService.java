package qwen.chat.platform.domain.qwen;

import qwen.chat.platform.domain.qwen.model.entity.CreateImageEntity;
import qwen.chat.platform.domain.qwen.model.entity.ResponseEntity;

public interface QwenCreateService {
    ResponseEntity handleImage(CreateImageEntity createImageEntity);
}
