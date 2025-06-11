package qwen.chat.platform.domain.login.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 退出登录状态枚举
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum LogOutStatusEnum {

    SUCCESS(0, "退出成功"),
    FAILED(1, "退出失败"),
    NULL_ID(1, "用户名为空"),
    OTHER_ID(1, "错误请求，不可退出其他用户"),
    ;
    private Integer code;
    private String info;
}
