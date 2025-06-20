package qwen.chat.platform.domain.qwen;

import lombok.extern.slf4j.Slf4j;
import qwen.chat.platform.domain.login.UserService;
import qwen.chat.platform.domain.qwen.adapter.repository.QwenRepository;
import qwen.chat.platform.domain.qwen.model.entity.CreateImageEntity;
import qwen.chat.platform.domain.qwen.model.entity.CreateVideoEntity;
import qwen.chat.platform.domain.qwen.model.entity.ResponseEntity;
import qwen.chat.platform.domain.qwen.model.valobj.CommandTypeEnum;
import qwen.chat.platform.domain.qwen.model.valobj.RoleConstant;
import qwen.chat.platform.domain.qwen.model.valobj.SizeTypeEnum;
import qwen.sdk.largemodel.chat.model.ChatRequest;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public abstract class DefaultQwenCreateService implements QwenCreateService {

    // 生成图片处理器映射
    private final Map<Integer, Function<CreateImageEntity, ResponseEntity>> imageHandlerMap = new HashMap<>();

    @Resource
    private QwenRepository qwenRepository;

    @PostConstruct
    public void init() {
        imageHandlerMap.put(CommandTypeEnum.CREATE_IMAGE.getType(), this::createImage);
        imageHandlerMap.put(CommandTypeEnum.DESCRIPTION_EDIT.getType(), this::descriptionEdit);
        imageHandlerMap.put(CommandTypeEnum.REMOVE_WATERMARK.getType(), this::removeWatermark);
        imageHandlerMap.put(CommandTypeEnum.EXPAND.getType(), this::expand);
        imageHandlerMap.put(CommandTypeEnum.SUPER_RESOLUTION.getType(), this::superResolution);
        imageHandlerMap.put(CommandTypeEnum.COLORIZATION.getType(), this::colorization);
        log.info("生成图片处理器初始化完成");
    }

    @Override
    public ResponseEntity handleImage(CreateImageEntity createImageEntity) {
        String userId = createImageEntity.getUserId();
        String historyCode = createImageEntity.getHistoryCode();
        String content = createImageEntity.getContent();
        String refer = createImageEntity.getRefer();
        Integer commandType = createImageEntity.getCommandType();
        Integer sizeType = createImageEntity.getSizeType();
        // 获取历史记录
        List<ChatRequest.Input.Message> historyMessage = qwenRepository.getHistory(userId, historyCode, true);
        // 获取请求记录
        List<ChatRequest.Input.Message> requestMessage = qwenRepository.getHistory(userId, historyCode, false);
        // 添加历史记录
        List<ChatRequest.Input.Message.Content> userContent = new ArrayList<>();
        userContent.add(ChatRequest.Input.Message.Content.builder()
                .text(CommandTypeEnum.getCommand(commandType) + ":" + content + "比例:" + SizeTypeEnum.getInfo(sizeType))
                .build());
        if (refer != null) {
            userContent.add(ChatRequest.Input.Message.Content.builder()
                    .image(refer)
                    .build());
        }
        historyMessage.add(ChatRequest.Input.Message.builder()
                .role(RoleConstant.USER)
                .content(userContent)
                .build());
        requestMessage.add(ChatRequest.Input.Message.builder()
                .role(RoleConstant.USER)
                .content(userContent)
                .build());
        createImageEntity.setHistoryMessage(historyMessage);
        createImageEntity.setRequestMessage(requestMessage);
        return imageHandlerMap.get(commandType).apply(createImageEntity);
    }

    @Override
    public ResponseEntity handleVideo(CreateVideoEntity createVideoEntity) {
        String userId = createVideoEntity.getUserId();
        String historyCode = createVideoEntity.getHistoryCode();
        String content = createVideoEntity.getContent();
        String firstFrameUrl = createVideoEntity.getFirstFrameUrl();
        String lastFrameUrl = createVideoEntity.getLastFrameUrl();
        List<ChatRequest.Input.Message> historyMessage = qwenRepository.getHistory(userId, historyCode, true);
        List<ChatRequest.Input.Message> requestMessage = qwenRepository.getHistory(userId, historyCode, true);
        List<ChatRequest.Input.Message.Content> userContent = new ArrayList<>();
        userContent.add(ChatRequest.Input.Message.Content.builder()
                .text("生成视频：" + content)
                .build());
        if (firstFrameUrl != null) {
            userContent.add(ChatRequest.Input.Message.Content.builder()
                    .image(firstFrameUrl)
                    .build());
        }
        if (lastFrameUrl != null) {
            userContent.add(ChatRequest.Input.Message.Content.builder()
                    .image(lastFrameUrl)
                    .build());
        }
        historyMessage.add(ChatRequest.Input.Message.builder()
                .role(RoleConstant.USER)
                .content(userContent)
                .build());
        requestMessage.add(ChatRequest.Input.Message.builder()
                .role(RoleConstant.USER)
                .content(userContent)
                .build());
        createVideoEntity.setHistoryMessage(historyMessage);
        createVideoEntity.setRequestMessage(requestMessage);
        return this.createVideo(createVideoEntity);
    }

    protected abstract ResponseEntity createImage(CreateImageEntity createImageEntity);

    protected abstract ResponseEntity descriptionEdit(CreateImageEntity createImageEntity);

    protected abstract ResponseEntity removeWatermark(CreateImageEntity createImageEntity);

    protected abstract ResponseEntity expand(CreateImageEntity createImageEntity);

    protected abstract ResponseEntity superResolution(CreateImageEntity createImageEntity);

    protected abstract ResponseEntity colorization(CreateImageEntity createImageEntity);

    protected abstract ResponseEntity createVideo(CreateVideoEntity createVideoEntity);
}
