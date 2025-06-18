package qwen.chat.platform.domain.login.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum CheckIsLoginEnum {

    SUCCESS(0, "校验成功"),
    FAILED(1, "校验失败"),
    NULL_ID(1, "用户名为空"),
    OTHER_ID(1, "非法请求：不可校验非自身用户"),
    ;
    private Integer code;
    private String info;
}
