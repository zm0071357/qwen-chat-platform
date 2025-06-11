package qwen.chat.platform.domain.login.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册结果
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResultEntity {
    private boolean isSuccess;
    private String message;
}
