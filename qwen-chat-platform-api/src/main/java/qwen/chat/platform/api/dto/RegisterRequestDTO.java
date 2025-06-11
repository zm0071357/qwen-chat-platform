package qwen.chat.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class RegisterRequestDTO {
    private String userId;
    private String password;
    @JsonProperty("confirm_password")
    private String confirmPassword;
}
