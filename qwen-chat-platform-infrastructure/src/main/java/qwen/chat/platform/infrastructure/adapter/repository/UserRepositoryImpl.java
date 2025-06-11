package qwen.chat.platform.infrastructure.adapter.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import qwen.chat.platform.domain.login.adapter.repository.UserRepository;
import qwen.chat.platform.domain.login.model.entity.LoginByAccEntity;
import qwen.chat.platform.domain.login.model.entity.RegisterEntity;
import qwen.chat.platform.domain.login.model.entity.RegisterResultEntity;
import qwen.chat.platform.domain.login.model.valobj.RegisterStatusEnum;
import qwen.chat.platform.infrastructure.dao.UserDao;
import qwen.chat.platform.infrastructure.dao.po.User;
import qwen.chat.platform.types.utils.AgronUtils;

import javax.annotation.Resource;

@Service
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    @Resource
    private UserDao userDAO;

    @Resource
    private AgronUtils agronUtils;

    @Override
    public RegisterResultEntity register(RegisterEntity registerEntity) {
        String userId = registerEntity.getUserId();
        String password = registerEntity.getPassword();
        User existUser = userDAO.getUserById(userId);
        if (existUser != null) {
            return RegisterResultEntity.builder()
                    .isSuccess(false)
                    .message(RegisterStatusEnum.REPEAT_ACCOUNT.getInfo())
                    .build();
        }
        String hashPassword = agronUtils.hashPassword(password);
        User newUser = User.builder()
                .userId(userId)
                .password(hashPassword)
                .build();
        int count = userDAO.insert(newUser);
        if (count > 0) {
            return RegisterResultEntity.builder()
                    .isSuccess(true)
                    .message(RegisterStatusEnum.SUCCESS.getInfo())
                    .build();
        }
        return RegisterResultEntity.builder()
                .isSuccess(false)
                .message(RegisterStatusEnum.UNKNOWN_FAILED.getInfo())
                .build();
    }

    @Override
    public boolean loginByAcc(LoginByAccEntity loginByAccEntity) {
        String userId = loginByAccEntity.getUserId();
        String password = loginByAccEntity.getPassword();
        User user = userDAO.getUserById(userId);
        if (user == null) {
            return false;
        }
        String hashPassword = user.getPassword();
        return agronUtils.verifyPassword(hashPassword, password);
    }
}
