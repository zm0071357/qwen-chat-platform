package qwen.chat.platform.trigger.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import qwen.chat.platform.api.ChatService;
import qwen.chat.platform.api.dto.ChatRequestDTO;
import qwen.chat.platform.api.dto.ChatResponseDTO;
import qwen.chat.platform.api.response.Response;
import qwen.chat.platform.domain.qwen.QwenService;
import qwen.chat.platform.domain.qwen.model.entity.MessageEntity;
import qwen.chat.platform.domain.qwen.model.valobj.ChatResultEnum;
import qwen.chat.platform.domain.qwen.model.valobj.File;
import qwen.chat.platform.domain.qwen.model.valobj.MessageTypeEnum;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/chat")
@CrossOrigin
@Slf4j
public class ChatController implements ChatService {

    @Resource
    private QwenService qwenService;

    @PostMapping
    @Override
    public ResponseBodyEmitter chat(@RequestBody ChatRequestDTO chatRequestDTO) {
        // 参数
        String userId = chatRequestDTO.getUserId();
        String content = chatRequestDTO.getContent();
        Integer msgType = chatRequestDTO.getMsgType();
        String historyCode = chatRequestDTO.getHistoryCode();
        List<File> file = chatRequestDTO.getFile();
        boolean think = chatRequestDTO.isThink();
        boolean search = chatRequestDTO.isSearch();
        // 参数校验
        if (userId == null || msgType == null || historyCode == null || (content == null && (file == null || file.isEmpty()))) {
            ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
            emitter.onError(throwable -> log.error(ChatResultEnum.NULL_PARAMETER.getInfo(), throwable));
        }
        // 请求
        MessageEntity messageEntity = MessageEntity.builder()
                .userId(userId)
                .content(content)
                .historyCode(historyCode)
                .think(think)
                .search(search)
                .build();
        // 普通对话
        if (file == null) {
            messageEntity.setMsgType(MessageTypeEnum.TEXT.getMsgType());
        }
        // 多模态对话
        else {
            messageEntity.setMsgType(MessageTypeEnum.FILE.getMsgType());
            messageEntity.setFileList(file);
        }
        // 大模型处理
        return qwenService.handle(messageEntity);
    }

}
