package qwen.chat.platform.domain.qwen;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import qwen.chat.platform.domain.qwen.model.entity.MessageEntity;
import qwen.chat.platform.domain.qwen.model.valobj.MessageTypeEnum;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Slf4j
public abstract class DefaultQwenService implements QwenService {

    // 消息类型处理器映射
    private final Map<Integer, Function<MessageEntity, ResponseBodyEmitter>> handlerMap = new HashMap<>();

    @PostConstruct
    public void init() {
        // 初始化处理器映射
        handlerMap.put(MessageTypeEnum.TEXT.getMsgType(), this::handleTextMessage);
        handlerMap.put(MessageTypeEnum.FILE.getMsgType(), this::handleFileMessage);
        log.info("消息处理器初始化完成");
    }

    @Override
    public ResponseBodyEmitter handle(MessageEntity messageEntity) {
        Integer msgType = messageEntity.getMsgType();
        return handlerMap.get(msgType).apply(messageEntity);
    }

    // 处理文本消息
    protected abstract ResponseBodyEmitter handleFileMessage(MessageEntity messageEntity);

    // 处理文件消息
    protected abstract ResponseBodyEmitter handleTextMessage(MessageEntity messageEntity);

}
