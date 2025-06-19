package qwen.chat.platform.trigger.http;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import qwen.chat.platform.api.ChatService;
import qwen.chat.platform.api.dto.ChatRequestDTO;
import qwen.chat.platform.api.dto.HistoryRequestDTO;
import qwen.chat.platform.api.dto.UploadFileResponseDTO;
import qwen.chat.platform.api.response.Response;
import qwen.chat.platform.domain.login.UserService;
import qwen.chat.platform.domain.qwen.QwenService;
import qwen.chat.platform.domain.qwen.model.entity.MessageEntity;
import qwen.chat.platform.domain.qwen.model.valobj.ChatResultEnum;
import qwen.chat.platform.domain.qwen.model.valobj.HistoryEnum;
import qwen.chat.platform.domain.qwen.model.valobj.MessageTypeEnum;
import qwen.chat.platform.domain.qwen.model.valobj.UploadFileResultEnum;
import qwen.chat.platform.types.utils.AliOSSUtils;
import qwen.sdk.largemodel.chat.model.ChatRequest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/chat")
@CrossOrigin
@Slf4j
@SaCheckLogin
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
        String historyCode = chatRequestDTO.getHistoryCode();
        List<String> fileList = chatRequestDTO.getFile();
        boolean think = chatRequestDTO.isThink();
        boolean search = chatRequestDTO.isSearch();
        // 参数校验
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
        if (userId == null || historyCode == null || (content == null && (fileList == null || fileList.isEmpty()))) {
            emitter.completeWithError(new RuntimeException(ChatResultEnum.NULL_PARAMETER.getInfo()));
            return emitter;
        }
        if (fileList != null && search) {
            emitter.completeWithError(new RuntimeException(ChatResultEnum.EXIST_FILE_SEARCH.getInfo()));
            return emitter;
        }
        if (!StpUtil.getLoginIdAsString().equals(userId)) {
            emitter.completeWithError(new RuntimeException(ChatResultEnum.ILLEGAL.getInfo()));
            return emitter;
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
        // 参数合法性校验
        if (!StpUtil.getLoginIdAsString().equals(userId)) {
            Response.<UploadFileResponseDTO>builder()
                    .code(String.valueOf(UploadFileResultEnum.ILLEGAL.getCode()))
                    .info(UploadFileResultEnum.ILLEGAL.getInfo())
                    .build();
        }
        if (userService.checkUserIsExist(userId)) {
            try {
                // 上传操作
                String url = aliOSSUtils.upload(file);
                log.info("url:{}", url);
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

    @GetMapping("/get_history_code_list/{userId}")
    @Override
    public Response<List<String>> getHistoryCodeList(@PathVariable("userId") String userId) {
        // 参数非空校验
        if (userId == null) {
            return Response.<List<String>>builder()
                    .code(String.valueOf(HistoryEnum.NULL_PARAMETER.getCode()))
                    .info(HistoryEnum.NULL_PARAMETER.getInfo())
                    .build();
        }
        // 参数合法性校验
        if (!StpUtil.getLoginIdAsString().equals(userId)) {
            return Response.<List<String>>builder()
                    .code(String.valueOf(HistoryEnum.ILLEAGAL.getCode()))
                    .info(HistoryEnum.ILLEAGAL.getInfo())
                    .build();
        }
        if (userService.checkUserIsExist(userId)) {
            List<String> historyCodeList = qwenService.getHistoryCodeList(userId);
            return Response.<List<String>>builder()
                    .code(String.valueOf(HistoryEnum.SUCCESS.getCode()))
                    .data(historyCodeList)
                    .info(HistoryEnum.SUCCESS.getInfo())
                    .build();
        } else {
            return Response.<List<String>>builder()
                    .code(String.valueOf(HistoryEnum.USER_NOT_EXIST.getCode()))
                    .info(HistoryEnum.USER_NOT_EXIST.getInfo())
                    .build();
        }
    }

    @PostMapping("/get_history")
    @Override
    public Response<List<ChatRequest.Input.Message>> getHistory(@RequestBody HistoryRequestDTO historyRequestDTO) {
        // 参数
        String userId = historyRequestDTO.getUserId();
        String historyCode = historyRequestDTO.getHistoryCode();
        // 参数非空校验
        if (userId == null || historyCode == null) {
            return Response.<List<ChatRequest.Input.Message>>builder()
                    .code(String.valueOf(HistoryEnum.NULL_PARAMETER.getCode()))
                    .info(HistoryEnum.NULL_PARAMETER.getInfo())
                    .build();
        }
        // 参数合法性校验
        if (!StpUtil.getLoginIdAsString().equals(userId)) {
            return Response.<List<ChatRequest.Input.Message>>builder()
                    .code(String.valueOf(HistoryEnum.ILLEAGAL.getCode()))
                    .info(HistoryEnum.ILLEAGAL.getInfo())
                    .build();
        }
        if (userService.checkUserIsExist(userId)) {
            List<ChatRequest.Input.Message> messages = qwenService.getHistory(userId, historyCode);
            return Response.<List<ChatRequest.Input.Message>>builder()
                    .code(String.valueOf(HistoryEnum.SUCCESS.getCode()))
                    .data(messages)
                    .info(HistoryEnum.SUCCESS.getInfo())
                    .build();
        } else {
            return Response.<List<ChatRequest.Input.Message>>builder()
                    .code(String.valueOf(HistoryEnum.USER_NOT_EXIST.getCode()))
                    .info(HistoryEnum.USER_NOT_EXIST.getInfo())
                    .build();
        }
    }

}
