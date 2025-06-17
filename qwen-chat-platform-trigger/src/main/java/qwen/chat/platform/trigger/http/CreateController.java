package qwen.chat.platform.trigger.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import qwen.chat.platform.api.CreateService;
import qwen.chat.platform.api.dto.ImageRequestDTO;
import qwen.chat.platform.api.dto.CreateResponseDTO;
import qwen.chat.platform.api.dto.VideoRequestDTO;
import qwen.chat.platform.api.response.Response;
import qwen.chat.platform.domain.login.UserService;
import qwen.chat.platform.domain.qwen.QwenCreateService;
import qwen.chat.platform.domain.qwen.model.entity.CreateImageEntity;
import qwen.chat.platform.domain.qwen.model.entity.CreateVideoEntity;
import qwen.chat.platform.domain.qwen.model.entity.ResponseEntity;
import qwen.chat.platform.domain.qwen.model.valobj.CommandTypeEnum;
import qwen.chat.platform.domain.qwen.model.valobj.CreateResultEnum;
import qwen.chat.platform.domain.qwen.model.valobj.SizeTypeEnum;

import javax.annotation.Resource;

@RestController
@RequestMapping("/create")
@CrossOrigin
@Slf4j
public class CreateController implements CreateService {

    @Resource
    private UserService userService;

    @Resource
    private QwenCreateService qwenCreateService;

    @PostMapping("/image")
    @Override
    public Response<CreateResponseDTO> createImage(@RequestBody ImageRequestDTO imageRequestDTO) {
        // 参数
        String userId = imageRequestDTO.getUserId();
        String historyCode = imageRequestDTO.getHistoryCode();
        Integer commandType = imageRequestDTO.getCommandType();
        String refer = imageRequestDTO.getRefer();
        String content = imageRequestDTO.getContent();
        Integer sizeType = imageRequestDTO.getSizeType();
        // 参数非空校验
        if (userId == null || commandType == null || content == null || content.isEmpty() || historyCode == null) {
            return Response.<CreateResponseDTO>builder()
                    .code(String.valueOf(CreateResultEnum.NULL_PARAMETER.getCode()))
                    .info(CreateResultEnum.NULL_PARAMETER.getInfo())
                    .build();
        }
        // 参数合法性校验
        if (CommandTypeEnum.getCommand(commandType) == null) {
            return Response.<CreateResponseDTO>builder()
                    .code(String.valueOf(CreateResultEnum.NOT_EXIST_PARAMETER.getCode()))
                    .info(CreateResultEnum.NOT_EXIST_PARAMETER.getInfo())
                    .build();
        }
        // 参考图-命令类型校验
        if (refer == null && !CommandTypeEnum.CREATE_IMAGE.getType().equals(commandType)) {
            return Response.<CreateResponseDTO>builder()
                    .code(String.valueOf(CreateResultEnum.NULL_REFER.getCode()))
                    .info(CreateResultEnum.NULL_REFER.getInfo())
                    .build();
        }
        if (userService.checkUserIsExist(userId)) {
            CreateImageEntity createImageEntity = CreateImageEntity.builder()
                    .userId(userId)
                    .historyCode(historyCode)
                    .content(content)
                    .commandType(commandType)
                    .refer(refer)
                    .sizeType(sizeType != null ? sizeType : SizeTypeEnum.ONE_ONE.getType())
                    .build();
            ResponseEntity responseEntity = qwenCreateService.handleImage(createImageEntity);
            return Response.<CreateResponseDTO>builder()
                    .code(responseEntity.isSuccess() ? String.valueOf(CreateResultEnum.SUCCESS.getCode()) : String.valueOf(CreateResultEnum.FAILED.getCode()))
                    .info(responseEntity.isSuccess() ? CreateResultEnum.SUCCESS.getInfo() : CreateResultEnum.FAILED.getInfo())
                    .data(responseEntity.isSuccess() ? CreateResponseDTO.builder()
                            .url(responseEntity.getResult())
                            .build() : null)
                    .build();
        } else {
            return Response.<CreateResponseDTO>builder()
                    .code(String.valueOf(CreateResultEnum.USER_NOT_EXIST.getCode()))
                    .info(CreateResultEnum.USER_NOT_EXIST.getInfo())
                    .build();
        }
    }

    @PostMapping("/video")
    @Override
    public Response<CreateResponseDTO> createVideo(@RequestBody VideoRequestDTO videoRequestDTO) {
        // 参数
        String userId = videoRequestDTO.getUserId();
        String content = videoRequestDTO.getContent();
        String historyCode = videoRequestDTO.getHistoryCode();
        String firstFrameUrl = videoRequestDTO.getFirstFrameUrl();
        String lastFrameUrl = videoRequestDTO.getLastFrameUrl();
        // 参数校验
        if (userId == null || content == null || content.isEmpty() || historyCode == null) {
            return Response.<CreateResponseDTO>builder()
                    .code(String.valueOf(CreateResultEnum.NULL_PARAMETER.getCode()))
                    .info(CreateResultEnum.NULL_PARAMETER.getInfo())
                    .build();
        }
        if (userService.checkUserIsExist(userId)) {
            CreateVideoEntity createVideoEntity = CreateVideoEntity.builder()
                    .userId(userId)
                    .content(content)
                    .historyCode(historyCode)
                    .firstFrameUrl(firstFrameUrl)
                    .lastFrameUrl(lastFrameUrl)
                    .build();
            ResponseEntity responseEntity = qwenCreateService.handleVideo(createVideoEntity);
            return Response.<CreateResponseDTO>builder()
                    .code(responseEntity.isSuccess() ? String.valueOf(CreateResultEnum.SUCCESS.getCode()) : String.valueOf(CreateResultEnum.FAILED.getCode()))
                    .info(responseEntity.isSuccess() ? CreateResultEnum.SUCCESS.getInfo() : CreateResultEnum.FAILED.getInfo())
                    .data(responseEntity.isSuccess() ? CreateResponseDTO.builder()
                            .url(responseEntity.getResult())
                            .build() : null)
                    .build();
        } else {
            return Response.<CreateResponseDTO>builder()
                    .code(String.valueOf(CreateResultEnum.USER_NOT_EXIST.getCode()))
                    .info(CreateResultEnum.USER_NOT_EXIST.getInfo())
                    .build();
        }
    }

}
