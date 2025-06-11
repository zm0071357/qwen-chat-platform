package qwen.chat.platform.domain.login.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 账密登录实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginByAccEntity {
    private String userId;
    private String password;
}
