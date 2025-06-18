package qwen.chat.platform.domain.login.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 请求登录状态枚举
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum LoginStatusEnum {

    SUCCESS(0, "登录成功"),
    FAILED(1, "登录失败，请检查账号或密码是否输入正确"),
    NULL_ACCOUNT(1, "登录失败，账号不能为空"),
    NULL_PASSWORD(1, "登录失败，密码不能为空"),
    WRONG_CAPTCHA(1, "登录失败，请检查验证码是否输入正确"),
    TIMEOUT_CAPTCHA(1, "登录失败，验证码已过期，请重新获取"),
    LOGINED(2, "已登录，不能重复登录"),
    ;

    private Integer code;
    private String info;

}
