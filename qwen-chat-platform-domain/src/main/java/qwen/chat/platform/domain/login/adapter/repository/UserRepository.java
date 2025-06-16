package qwen.chat.platform.domain.login.adapter.repository;

import qwen.chat.platform.domain.login.model.entity.LoginByAccEntity;
import qwen.chat.platform.domain.login.model.entity.RegisterEntity;
import qwen.chat.platform.domain.login.model.entity.RegisterResultEntity;

public interface UserRepository {

    /**
     * 注册
     * @param registerEntity
     * @return
     */
    RegisterResultEntity register(RegisterEntity registerEntity);

    /**
     * 查询记录
     * @param loginByAccEntity
     * @return
     */
     boolean loginByAcc(LoginByAccEntity loginByAccEntity);

    /**
     * 查询记录数
     * @param userId
     * @return
     */
    int checkUserIsExist(String userId);
}
