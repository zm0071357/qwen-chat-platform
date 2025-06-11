package qwen.chat.platform.domain.login.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterEntity {
    private String userId;
    private String password;
}
