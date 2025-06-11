package qwen.chat.platform.api.dto;

import lombok.Getter;

@Getter
public class LoginByVCRequestDTO {
    private String phone;

    private String CAPTCHA;
}
