package qwen.chat.platform.trigger.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import qwen.chat.platform.api.ImageService;
import qwen.chat.platform.api.dto.ImageRequestDTO;
import qwen.chat.platform.api.dto.ImageResponseDTO;
import qwen.chat.platform.api.response.Response;
import qwen.chat.platform.domain.login.UserService;
import qwen.chat.platform.domain.qwen.QwenCreateService;
import qwen.chat.platform.domain.qwen.model.entity.CreateImageEntity;
import qwen.chat.platform.domain.qwen.model.entity.ResponseEntity;
import qwen.chat.platform.domain.qwen.model.valobj.CommandTypeEnum;
import qwen.chat.platform.domain.qwen.model.valobj.ImageResultEnum;
import qwen.chat.platform.domain.qwen.model.valobj.SizeTypeEnum;

import javax.annotation.Resource;

@RestController
@RequestMapping("/create")
@CrossOrigin
@Slf4j
public class CreateController implements ImageService {

    @Resource
    private UserService userService;

    @Resource
    private QwenCreateService qwenCreateService;

    @PostMapping("/image")
    @Override
    public Response<ImageResponseDTO> createImage(@RequestBody ImageRequestDTO imageRequestDTO) {
        // 参数
        String userId = imageRequestDTO.getUserId();
        String historyCode = imageRequestDTO.getHistoryCode();
        Integer commandType = imageRequestDTO.getCommandType();
        String refer = imageRequestDTO.getRefer();
        String content = imageRequestDTO.getContent();
        Integer sizeType = imageRequestDTO.getSizeType();
        // 参数非空校验
        if (userId == null || commandType == null || content == null || content.isEmpty() || historyCode == null) {
            return Response.<ImageResponseDTO>builder()
                    .code(String.valueOf(ImageResultEnum.NULL_PARAMETER.getCode()))
                    .info(ImageResultEnum.NULL_PARAMETER.getInfo())
                    .build();
        }
        // 参数合法性校验
        if (CommandTypeEnum.getCommand(commandType) == null) {
            return Response.<ImageResponseDTO>builder()
                    .code(String.valueOf(ImageResultEnum.NOT_EXIST_PARAMETER.getCode()))
                    .info(ImageResultEnum.NOT_EXIST_PARAMETER.getInfo())
                    .build();
        }
        // 参考图-命令类型校验
        if (refer == null && !CommandTypeEnum.CREATE_IMAGE.getType().equals(commandType)) {
            return Response.<ImageResponseDTO>builder()
                    .code(String.valueOf(ImageResultEnum.NULL_REFER.getCode()))
                    .info(ImageResultEnum.NULL_REFER.getInfo())
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
            return Response.<ImageResponseDTO>builder()
                    .code(responseEntity.isSuccess() ? String.valueOf(ImageResultEnum.SUCCESS.getCode()) : String.valueOf(ImageResultEnum.FAILED.getCode()))
                    .info(responseEntity.isSuccess() ? ImageResultEnum.SUCCESS.getInfo() : ImageResultEnum.FAILED.getInfo())
                    .data(responseEntity.isSuccess() ? ImageResponseDTO.builder()
                            .url(responseEntity.getResult())
                            .build() : null)
                    .build();
        } else {
            return Response.<ImageResponseDTO>builder()
                    .code(String.valueOf(ImageResultEnum.USER_NOT_EXIST.getCode()))
                    .info(ImageResultEnum.USER_NOT_EXIST.getInfo())
                    .build();
        }
    }

}
