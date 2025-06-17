package qwen.chat.platform.infrastructure.adapter.repository;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import qwen.chat.platform.domain.qwen.adapter.repository.QwenCreateRepository;
import qwen.chat.platform.domain.qwen.model.entity.CreateImageEntity;
import qwen.chat.platform.domain.qwen.model.entity.ResponseEntity;
import qwen.chat.platform.domain.qwen.model.valobj.CommandTypeEnum;
import qwen.chat.platform.domain.qwen.model.valobj.MessageConstant;
import qwen.chat.platform.domain.qwen.model.valobj.RoleConstant;
import qwen.chat.platform.domain.qwen.model.valobj.SizeTypeEnum;
import qwen.chat.platform.infrastructure.dao.QwenDao;
import qwen.chat.platform.infrastructure.dao.po.History;
import qwen.sdk.largemodel.chat.model.ChatRequest;
import qwen.sdk.largemodel.image.enums.ImageEnum;
import qwen.sdk.largemodel.image.enums.ImageTaskStatusEnum;
import qwen.sdk.largemodel.image.impl.ImageServiceImpl;
import qwen.sdk.largemodel.image.model.ImageRequest;
import qwen.sdk.largemodel.image.model.ImageResponse;
import qwen.sdk.largemodel.image.model.ResultResponse;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class QwenCreateRepositoryImpl implements QwenCreateRepository {

    @Resource
    private QwenDao qwenDao;

    private final ImageServiceImpl imageServiceImpl;

    public QwenCreateRepositoryImpl(ImageServiceImpl imageServiceImpl) {
        this.imageServiceImpl = imageServiceImpl;
    }

    @Override
    public ResponseEntity createImage(CreateImageEntity createImageEntity) {
        // 构造参数
        String userId = createImageEntity.getUserId();
        String historyCode = createImageEntity.getHistoryCode();
        String content = createImageEntity.getContent();
        Integer sizeType = createImageEntity.getSizeType();
        List<ChatRequest.Input.Message> history = createImageEntity.getHistory();
        ImageRequest imageRequest = ImageRequest.builder()
                .model(ImageEnum.WANX_21_T2I_TURBO.getModel())
                .input(ImageRequest.Input.builder().prompt(content).build())
                .parameters(ImageRequest.Parameters.builder()
                        .prompt_extend(true)
                        .size(SizeTypeEnum.getSize(sizeType))
                        .n(1)
                        .build())
                .build();
        return this.imageHandle(imageRequest, history, userId, historyCode);
    }

    @Override
    public ResponseEntity descriptionEdit(CreateImageEntity createImageEntity) {
        // 构造参数
        String userId = createImageEntity.getUserId();
        String historyCode = createImageEntity.getHistoryCode();
        String content = createImageEntity.getContent();
        String refer = createImageEntity.getRefer();
        List<ChatRequest.Input.Message> history = createImageEntity.getHistory();
        ImageRequest imageRequest = ImageRequest.builder()
                .model(ImageEnum.WANX_21_T2I_TURBO.getModel())
                .input(ImageRequest.InputExtend.builder()
                        .function(CommandTypeEnum.DESCRIPTION_EDIT.getFunction())
                        .prompt(content)
                        .base_image_url(refer)
                        .build())
                .parameters(ImageRequest.ParametersExtend.builder()
                        .strength(0.3F)
                        .n(1)
                        .build())
                .build();
        return this.imageHandle(imageRequest, history, userId, historyCode);
    }

    @Override
    public ResponseEntity removeWatermark(CreateImageEntity createImageEntity) {
        // 构造参数
        String userId = createImageEntity.getUserId();
        String historyCode = createImageEntity.getHistoryCode();
        String content = createImageEntity.getContent();
        String refer = createImageEntity.getRefer();
        List<ChatRequest.Input.Message> history = createImageEntity.getHistory();
        ImageRequest imageRequest = ImageRequest.builder()
                .model(ImageEnum.WANX_21_T2I_TURBO.getModel())
                .input(ImageRequest.InputExtend.builder()
                        .function(CommandTypeEnum.REMOVE_WATERMARK.getFunction())
                        .prompt(content)
                        .base_image_url(refer)
                        .build())
                .parameters(ImageRequest.Parameters.builder()
                        .n(1)
                        .build())
                .build();
        return this.imageHandle(imageRequest, history, userId, historyCode);
    }

    @Override
    public ResponseEntity expand(CreateImageEntity createImageEntity) {
        // 构造参数
        String userId = createImageEntity.getUserId();
        String historyCode = createImageEntity.getHistoryCode();
        String content = createImageEntity.getContent();
        String refer = createImageEntity.getRefer();
        List<ChatRequest.Input.Message> history = createImageEntity.getHistory();
        ImageRequest imageRequest = ImageRequest.builder()
                .model(ImageEnum.WANX_21_T2I_TURBO.getModel())
                .input(ImageRequest.InputExtend.builder()
                        .function(CommandTypeEnum.EXPAND.getFunction())
                        .prompt(content)
                        .base_image_url(refer)
                        .build())
                .parameters(ImageRequest.ParametersExtend.builder()
                        .right_scale(1.5F)
                        .left_scale(1.5F)
                        .top_scale(1.5F)
                        .bottom_scale(1.5F)
                        .n(1)
                        .build())
                .build();
        return this.imageHandle(imageRequest, history, userId, historyCode);
    }

    @Override
    public ResponseEntity superResolution(CreateImageEntity createImageEntity) {
        // 构造参数
        String userId = createImageEntity.getUserId();
        String historyCode = createImageEntity.getHistoryCode();
        String content = createImageEntity.getContent();
        String refer = createImageEntity.getRefer();
        List<ChatRequest.Input.Message> history = createImageEntity.getHistory();
        ImageRequest imageRequest = ImageRequest.builder()
                .model(ImageEnum.WANX_21_T2I_TURBO.getModel())
                .input(ImageRequest.InputExtend.builder()
                        .function(CommandTypeEnum.SUPER_RESOLUTION.getFunction())
                        .prompt(content)
                        .base_image_url(refer)
                        .build())
                .parameters(ImageRequest.Parameters.builder()
                        .n(1)
                        .build())
                .build();
        return this.imageHandle(imageRequest, history, userId, historyCode);
    }

    @Override
    public ResponseEntity colorization(CreateImageEntity createImageEntity) {
        // 构造参数
        String userId = createImageEntity.getUserId();
        String historyCode = createImageEntity.getHistoryCode();
        String content = createImageEntity.getContent();
        String refer = createImageEntity.getRefer();
        List<ChatRequest.Input.Message> history = createImageEntity.getHistory();
        ImageRequest imageRequest = ImageRequest.builder()
                .model(ImageEnum.WANX_21_T2I_TURBO.getModel())
                .input(ImageRequest.InputExtend.builder()
                        .function(CommandTypeEnum.COLORIZATION.getFunction())
                        .prompt(content)
                        .base_image_url(refer)
                        .build())
                .parameters(ImageRequest.Parameters.builder()
                        .n(1)
                        .build())
                .build();
        return this.imageHandle(imageRequest, history, userId, historyCode);
    }

    /**
     * 通用图片处理
     * @param imageRequest
     * @param history
     * @param userId
     * @param historyCode
     * @return
     */
    private ResponseEntity imageHandle(ImageRequest imageRequest, List<ChatRequest.Input.Message> history, String userId, String historyCode) {
        try {
            ImageResponse response = imageServiceImpl.imageSynthesis(imageRequest);
            String taskId = response.getOutput().getTask_id();
            String curStatus = ImageTaskStatusEnum.RUNNING.getCode();
            ResultResponse result = null;
            int count = 0;
            int maxCount = 150;
            // 轮询获取任务结果
            while (count < maxCount || ImageTaskStatusEnum.RUNNING.getCode().equals(curStatus)) {
                result = imageServiceImpl.result(taskId);
                curStatus = result.getOutput().getTask_status();
                count += 1;
                log.info("请求次数:{},结果:{}", count, curStatus);
                if (ImageTaskStatusEnum.SUCCEEDED.getCode().equals(curStatus)) {
                    break;
                } else if (ImageTaskStatusEnum.FAILED.getCode().equals(curStatus) || ImageTaskStatusEnum.UNKNOWN.getCode().equals(curStatus)) {
                    history.remove(history.size() - 1);
                    return ResponseEntity.builder()
                            .isSuccess(false)
                            .message(MessageConstant.IMAGE_FAILED_MESSAGE)
                            .build();
                }
            }
            String url = result.getOutput().getResults().get(0).getUrl();
            log.info("url:{}", url);
            // 添加历史记录
            List<ChatRequest.Input.Message.Content> systemContent = new ArrayList<>();
            systemContent.add(ChatRequest.Input.Message.Content.builder()
                    .image(url)
                    .build());
            history.add(ChatRequest.Input.Message.builder()
                    .role(RoleConstant.SYSTEM)
                    .content(systemContent)
                    .build());
            log.info("history:{}", JSON.toJSONString(history));
            // 更新历史记录
            qwenDao.update(History.builder()
                    .userId(userId)
                    .historyCode(historyCode)
                    .historyJson(JSON.toJSONString(history))
                    .build());
            return ResponseEntity.builder()
                    .isSuccess(true)
                    .result(url)
                    .build();
        } catch (IOException e) {
            history.remove(history.size() - 1);
            return ResponseEntity.builder()
                    .isSuccess(false)
                    .message(MessageConstant.IMAGE_FAILED_MESSAGE)
                    .build();
        }
    }
}
