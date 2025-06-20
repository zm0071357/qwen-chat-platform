package qwen.chat.platform.infrastructure.adapter.repository;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import qwen.chat.platform.domain.qwen.adapter.repository.QwenCreateRepository;
import qwen.chat.platform.domain.qwen.model.entity.CreateImageEntity;
import qwen.chat.platform.domain.qwen.model.entity.CreateVideoEntity;
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
import qwen.sdk.largemodel.video.enums.VideoModelEnum;
import qwen.sdk.largemodel.video.enums.VideoTaskStatusEnum;
import qwen.sdk.largemodel.video.impl.VideoServiceImpl;
import qwen.sdk.largemodel.video.model.VideoRequest;
import qwen.sdk.largemodel.video.model.VideoResponse;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class QwenCreateRepositoryImpl implements QwenCreateRepository {

    @Resource
    private QwenDao qwenDao;

    private final ImageServiceImpl imageServiceImpl;

    private final VideoServiceImpl videoServiceImpl;

    public QwenCreateRepositoryImpl(ImageServiceImpl imageServiceImpl, VideoServiceImpl videoServiceImpl) {
        this.imageServiceImpl = imageServiceImpl;
        this.videoServiceImpl = videoServiceImpl;
    }

    @Override
    public ResponseEntity createImage(CreateImageEntity createImageEntity) {
        // 构造参数
        String content = createImageEntity.getContent();
        Integer sizeType = createImageEntity.getSizeType();
        ImageRequest imageRequest = ImageRequest.builder()
                .model(ImageEnum.WANX_21_T2I_TURBO.getModel())
                .input(ImageRequest.Input.builder().prompt(content).build())
                .parameters(ImageRequest.Parameters.builder()
                        .prompt_extend(true)
                        .size(SizeTypeEnum.getSize(sizeType))
                        .n(1)
                        .build())
                .build();
        return this.imageHandle(imageRequest, createImageEntity);
    }

    @Override
    public ResponseEntity descriptionEdit(CreateImageEntity createImageEntity) {
        // 构造参数
        String content = createImageEntity.getContent();
        String refer = createImageEntity.getRefer();
        ImageRequest imageRequest = ImageRequest.builder()
                .model(ImageEnum.WANX_21_T2I_TURBO.getModel())
                .input(ImageRequest.InputExtend.builder()
                        .function(CommandTypeEnum.DESCRIPTION_EDIT.getFunction())
                        .prompt(content)
                        .base_image_url(refer)
                        .build())
                .parameters(ImageRequest.ParametersExtend.builder()
                        .strength(0.7F)
                        .n(1)
                        .build())
                .build();
        return this.imageHandle(imageRequest, createImageEntity);
    }

    @Override
    public ResponseEntity removeWatermark(CreateImageEntity createImageEntity) {
        // 构造参数
        String content = createImageEntity.getContent();
        String refer = createImageEntity.getRefer();
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
        return this.imageHandle(imageRequest, createImageEntity);
    }

    @Override
    public ResponseEntity expand(CreateImageEntity createImageEntity) {
        // 构造参数
        String content = createImageEntity.getContent();
        String refer = createImageEntity.getRefer();
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
        return this.imageHandle(imageRequest, createImageEntity);
    }

    @Override
    public ResponseEntity superResolution(CreateImageEntity createImageEntity) {
        // 构造参数
        String content = createImageEntity.getContent();
        String refer = createImageEntity.getRefer();
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
        return this.imageHandle(imageRequest, createImageEntity);
    }

    @Override
    public ResponseEntity colorization(CreateImageEntity createImageEntity) {
        // 构造参数
        String content = createImageEntity.getContent();
        String refer = createImageEntity.getRefer();
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
        return this.imageHandle(imageRequest, createImageEntity);
    }

    @Override
    public ResponseEntity createVideo(CreateVideoEntity createVideoEntity) {
        String content = createVideoEntity.getContent();
        String firstFrameUrl = createVideoEntity.getFirstFrameUrl();
        String lastFrameUrl = createVideoEntity.getLastFrameUrl();
        VideoRequest videoRequest = VideoRequest.builder()
                .model(VideoModelEnum.WANX_21_T2V_TURBO.getModel())
                .input(VideoRequest.InputExtend.builder()
                        .firstFrameUrl(firstFrameUrl)
                        .lastFrameUrl(lastFrameUrl)
                        .prompt(content)
                        .build())
                .parameters(VideoRequest.ParametersExtend.builder()
                        .promptExtend(true)
                        .build())
                .build();
        return this.videoHandle(videoRequest, createVideoEntity);
    }

    /**
     * 通用图片处理
     * @param imageRequest
     * @param createImageEntity
     * @return
     */
    private ResponseEntity imageHandle(ImageRequest imageRequest, CreateImageEntity createImageEntity) {
        String userId = createImageEntity.getUserId();
        String historyCode = createImageEntity.getHistoryCode();
        List<ChatRequest.Input.Message> historyMessage = createImageEntity.getHistoryMessage();
        List<ChatRequest.Input.Message> requestMessage = createImageEntity.getRequestMessage();
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
                    historyMessage.remove(historyMessage.size() - 1);
                    requestMessage.remove(requestMessage.size() - 1);
                    return ResponseEntity.builder()
                            .isSuccess(false)
                            .message(MessageConstant.IMAGE_FAILED_MESSAGE)
                            .build();
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    historyMessage.remove(historyMessage.size() - 1);
                    requestMessage.remove(requestMessage.size() - 1);
                    return ResponseEntity.builder()
                            .isSuccess(false)
                            .message(MessageConstant.IMAGE_FAILED_MESSAGE)
                            .build();
                }
            }
            String url = result.getOutput().getResults().get(0).getUrl();
            log.info("url:{}", url);
            // 添加历史记录
            List<ChatRequest.Input.Message.Content> content = new ArrayList<>();
            content.add(ChatRequest.Input.Message.Content.builder()
                    .image(url)
                    .build());
            historyMessage.add(ChatRequest.Input.Message.builder()
                    .role(RoleConstant.SYSTEM)
                    .content(content)
                    .build());
            requestMessage.add(ChatRequest.Input.Message.builder()
                    .role(RoleConstant.USER)
                    .content(content)
                    .build());
            // 更新历史记录
            qwenDao.update(History.builder()
                    .userId(userId)
                    .historyCode(historyCode)
                    .historyJson(JSON.toJSONString(historyMessage))
                    .requestJson(JSON.toJSONString(requestMessage))
                    .build());
            log.info("更新历史记录完成");
            return ResponseEntity.builder()
                    .isSuccess(true)
                    .result(url)
                    .build();
        } catch (IOException e) {
            historyMessage.remove(historyMessage.size() - 1);
            requestMessage.remove(requestMessage.size() - 1);
            return ResponseEntity.builder()
                    .isSuccess(false)
                    .message(MessageConstant.IMAGE_FAILED_MESSAGE)
                    .build();
        }
    }

    /**
     * 通用视频处理
     * @param videoRequest
     * @param createVideoEntity
     * @return
     */
    private ResponseEntity videoHandle(VideoRequest videoRequest, CreateVideoEntity createVideoEntity) {
        String userId = createVideoEntity.getUserId();
        String historyCode = createVideoEntity.getHistoryCode();
        List<ChatRequest.Input.Message> historyMessage = createVideoEntity.getHistoryMessage();
        List<ChatRequest.Input.Message> requestMessage = createVideoEntity.getRequestMessage();
        try {
            VideoResponse response = videoServiceImpl.videoSynthesis(videoRequest);
            String taskId = response.getOutput().getTask_id();
            String curStatus = ImageTaskStatusEnum.RUNNING.getCode();
            qwen.sdk.largemodel.video.model.ResultResponse result = null;
            int count = 0;
            int maxCount = 150;
            // 轮询获取任务结果
            while (count < maxCount || VideoTaskStatusEnum.RUNNING.getCode().equals(curStatus)) {
                result = videoServiceImpl.result(taskId);
                curStatus = result.getOutput().getTask_status();
                count += 1;
                log.info("请求次数:{},结果:{}", count, curStatus);
                if (VideoTaskStatusEnum.SUCCEEDED.getCode().equals(curStatus)) {
                    break;
                } else if (VideoTaskStatusEnum.FAILED.getCode().equals(curStatus) || VideoTaskStatusEnum.UNKNOWN.getCode().equals(curStatus)) {
                    historyMessage.remove(historyMessage.size() - 1);
                    requestMessage.remove(requestMessage.size() - 1);
                    return ResponseEntity.builder()
                            .isSuccess(false)
                            .message(MessageConstant.VIDEO_FAILED_MESSAGE)
                            .build();
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    historyMessage.remove(historyMessage.size() - 1);
                    requestMessage.remove(requestMessage.size() - 1);
                    return ResponseEntity.builder()
                            .isSuccess(false)
                            .message(MessageConstant.VIDEO_FAILED_MESSAGE)
                            .build();
                }
            }
            String url = result.getOutput().getVideo_url();
            log.info("url:{}", url);
            // 添加历史记录
            List<ChatRequest.Input.Message.Content> content = new ArrayList<>();
            content.add(ChatRequest.Input.Message.Content.builder()
                    .video(url)
                    .build());
            historyMessage.add(ChatRequest.Input.Message.builder()
                    .role(RoleConstant.SYSTEM)
                    .content(content)
                    .build());
            requestMessage.remove(requestMessage.size() - 1);
            // 更新历史记录
            qwenDao.update(History.builder()
                    .userId(userId)
                    .historyCode(historyCode)
                    .historyJson(JSON.toJSONString(historyMessage))
                    .requestJson(JSON.toJSONString(requestMessage))
                    .build());
            log.info("更新历史记录完成");
            return ResponseEntity.builder()
                    .isSuccess(true)
                    .result(url)
                    .build();


        } catch (IOException e) {
            log.info("生成视频出错:{}", e.getMessage());
            historyMessage.remove(historyMessage.size() - 1);
            requestMessage.remove(requestMessage.size() - 1);
            return ResponseEntity.builder()
                    .isSuccess(false)
                    .message(MessageConstant.VIDEO_FAILED_MESSAGE)
                    .build();
        }
    }

}
