package qwen.chat.platform.domain.qwen.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import qwen.chat.platform.domain.qwen.DefaultQwenCreateService;
import qwen.chat.platform.domain.qwen.adapter.repository.QwenCreateRepository;
import qwen.chat.platform.domain.qwen.model.entity.CreateImageEntity;
import qwen.chat.platform.domain.qwen.model.entity.CreateVideoEntity;
import qwen.chat.platform.domain.qwen.model.entity.ResponseEntity;

import javax.annotation.Resource;

@Service
@Slf4j
public class QwenCreateServiceImpl extends DefaultQwenCreateService {

    @Resource
    private QwenCreateRepository qwenCreateRepository;

    @Override
    protected ResponseEntity createImage(CreateImageEntity createImageEntity) {
        return qwenCreateRepository.createImage(createImageEntity);
    }

    @Override
    protected ResponseEntity descriptionEdit(CreateImageEntity createImageEntity) {
        return qwenCreateRepository.descriptionEdit(createImageEntity);
    }

    @Override
    protected ResponseEntity removeWatermark(CreateImageEntity createImageEntity) {
        return qwenCreateRepository.removeWatermark(createImageEntity);
    }

    @Override
    protected ResponseEntity expand(CreateImageEntity createImageEntity) {
        return qwenCreateRepository.expand(createImageEntity);
    }

    @Override
    protected ResponseEntity superResolution(CreateImageEntity createImageEntity) {
        return qwenCreateRepository.superResolution(createImageEntity);
    }

    @Override
    protected ResponseEntity colorization(CreateImageEntity createImageEntity) {
        return qwenCreateRepository.colorization(createImageEntity);
    }

    @Override
    protected ResponseEntity createVideo(CreateVideoEntity createVideoEntity) {
        return qwenCreateRepository.createVideo(createVideoEntity);
    }
}
