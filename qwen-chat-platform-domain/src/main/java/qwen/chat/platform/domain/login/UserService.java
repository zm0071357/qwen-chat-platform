package qwen.chat.platform.domain.login;

import qwen.chat.platform.domain.login.model.entity.LoginByAccEntity;
import qwen.chat.platform.domain.login.model.entity.LoginResultEntity;
import qwen.chat.platform.domain.login.model.entity.RegisterEntity;

public interface UserService {

    /**
     * 注册
     * @param registerEntity
     * @return
     */
    LoginResultEntity register(RegisterEntity registerEntity);

    /**
     * 使用账密登录
     * @param loginByAccEntity
     * @return
     */
    LoginResultEntity loginByAcc(LoginByAccEntity loginByAccEntity);

    /**
     * 判断用户是否存在
     * @param userId
     * @return
     */
    boolean checkUserIsExist(String userId);
}
