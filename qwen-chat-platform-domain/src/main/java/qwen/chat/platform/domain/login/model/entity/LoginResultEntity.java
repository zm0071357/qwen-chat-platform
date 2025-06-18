package qwen.chat.platform.domain.login.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录结果实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResultEntity {

    private boolean isSuccess;

    private String message;

    private String token;
    private String tokenName;

    private long timeout;

}
