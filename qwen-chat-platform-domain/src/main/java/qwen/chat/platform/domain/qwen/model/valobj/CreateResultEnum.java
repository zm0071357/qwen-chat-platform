package qwen.chat.platform.domain.qwen.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum CreateResultEnum {
    SUCCESS(0, "请求成功"),
    FAILED(1, "请求失败，请稍后再试"),
    NULL_PARAMETER(1, "非法请求：缺少必要参数"),
    NULL_REFER(1, "非法请求：缺少参考图"),
    NOT_EXIST_PARAMETER(1, "非法请求：参数不存在"),
    SENSITIVE_WORDS(1, "非法请求：存在敏感词"),
    USER_NOT_EXIST(1, "用户不存在"),
    ;
    private Integer code;
    private String info;
}
