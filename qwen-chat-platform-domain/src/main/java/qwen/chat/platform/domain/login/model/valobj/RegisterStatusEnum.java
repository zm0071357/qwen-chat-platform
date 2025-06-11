package qwen.chat.platform.domain.login.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 注册状态枚举
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum RegisterStatusEnum {

    SUCCESS(0, "注册成功"),
    FAILED(1, "注册失败"),
    NULL_ACCOUNT(1, "用户名不能为空"),
    NULL_PASSWORD(1, "密码不能为空"),
    INCONSISTENT_PASSWORD(1, "两次密码不一致"),
    REPEAT_ACCOUNT(1, "用户名重复"),
    UNKNOWN_FAILED(1, "未知原因失败"),
    ;

    private Integer code;
    private String info;
}
