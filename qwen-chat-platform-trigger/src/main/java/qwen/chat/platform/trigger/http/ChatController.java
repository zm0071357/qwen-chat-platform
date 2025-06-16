package qwen.chat.platform.trigger.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import qwen.chat.platform.api.ChatService;
import qwen.chat.platform.api.dto.ChatRequestDTO;
import qwen.chat.platform.api.dto.UploadFileResponseDTO;
import qwen.chat.platform.api.response.Response;
import qwen.chat.platform.domain.login.UserService;
import qwen.chat.platform.domain.qwen.QwenService;
import qwen.chat.platform.domain.qwen.model.entity.MessageEntity;
import qwen.chat.platform.domain.qwen.model.valobj.ChatResultEnum;
import qwen.chat.platform.domain.qwen.model.valobj.FileTypeEnum;
import qwen.chat.platform.domain.qwen.model.valobj.MessageTypeEnum;
import qwen.chat.platform.domain.qwen.model.valobj.UploadFileResultEnum;
import qwen.chat.platform.types.utils.AliOSSUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/chat")
@CrossOrigin
@Slf4j
public class ChatController implements ChatService {

    @Resource
    private QwenService qwenService;

    @Resource
    private AliOSSUtils aliOSSUtils;

    @Resource
    private UserService userService;

    @PostMapping
    @Override
    public ResponseBodyEmitter chat(@RequestBody ChatRequestDTO chatRequestDTO) {
        // 参数
        String userId = chatRequestDTO.getUserId();
        String content = chatRequestDTO.getContent();
        Integer msgType = chatRequestDTO.getMsgType();
        String historyCode = chatRequestDTO.getHistoryCode();
        List<String> fileList = chatRequestDTO.getFile();
        boolean think = chatRequestDTO.isThink();
        boolean search = chatRequestDTO.isSearch();
        // 参数校验
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
        if (userId == null || msgType == null || historyCode == null || (content == null && (fileList == null || fileList.isEmpty()))) {
            emitter.onError(throwable -> log.error(ChatResultEnum.NULL_PARAMETER.getInfo(), throwable));
        }
        if (fileList != null && search) {
            emitter.onError(throwable -> log.error(ChatResultEnum.EXIST_FILE_SEARCH.getInfo(), throwable));
        }

        // 请求
        MessageEntity messageEntity = MessageEntity.builder()
                .userId(userId)
                .content(content)
                .historyCode(historyCode)
                .think(think)
                .search(search)
                .build();
        messageEntity.setMsgType(fileList == null ? MessageTypeEnum.TEXT.getMsgType() : MessageTypeEnum.FILE.getMsgType());
        messageEntity.setFileList(fileList);
        // 大模型处理
        return qwenService.handle(messageEntity);
    }

    @PostMapping("/upload_file")
    @Override
    public Response<UploadFileResponseDTO> uploadFile(@RequestParam("file") MultipartFile file,
                                                      @RequestParam("userId") String userId) {
        // 参数校验
        if (file == null || userId == null) {
            return Response.<UploadFileResponseDTO>builder()
                            .code(String.valueOf(UploadFileResultEnum.NULL_PARAMETER.getCode()))
                            .info(UploadFileResultEnum.NULL_PARAMETER.getInfo())
                            .build();
        }
        log.info("fileSize:{}", file.getSize());
        if (userService.checkUserIsExist(userId)) {
            try {
                // 上传操作
                String url = aliOSSUtils.upload(file);
                return Response.<UploadFileResponseDTO>builder()
                        .code(String.valueOf(UploadFileResultEnum.SUCCESS.getCode()))
                        .data(UploadFileResponseDTO.builder()
                                .url(url)
                                .build())
                        .info(UploadFileResultEnum.SUCCESS.getInfo())
                        .build();
            } catch (IOException e) {
                return Response.<UploadFileResponseDTO>builder()
                        .code(String.valueOf(UploadFileResultEnum.FAILED.getCode()))
                        .info(UploadFileResultEnum.FAILED.getInfo())
                        .build();
            }
        } else {
            return Response.<UploadFileResponseDTO>builder()
                    .code(String.valueOf(UploadFileResultEnum.USER_NOT_EXIST.getCode()))
                    .info(UploadFileResultEnum.USER_NOT_EXIST.getInfo())
                    .build();
        }
    }

}
