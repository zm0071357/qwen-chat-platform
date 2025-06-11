package qwen.chat.platform.api.dto;

import lombok.Getter;

/**
 * 登录请求对象
 */
@Getter
public class LoginByAccRequestDTO {
    private String userId;
    private String password;
}
