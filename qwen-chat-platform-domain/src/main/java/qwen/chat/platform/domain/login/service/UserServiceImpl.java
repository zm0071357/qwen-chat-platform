package qwen.chat.platform.domain.login.service;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import qwen.chat.platform.domain.login.UserService;
import qwen.chat.platform.domain.login.adapter.repository.UserRepository;
import qwen.chat.platform.domain.login.model.entity.LoginByAccEntity;
import qwen.chat.platform.domain.login.model.entity.LoginResultEntity;
import qwen.chat.platform.domain.login.model.entity.RegisterEntity;
import qwen.chat.platform.domain.login.model.entity.RegisterResultEntity;

import javax.annotation.Resource;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserRepository userRepository;

    @Override
    public LoginResultEntity register(RegisterEntity registerEntity) {
        RegisterResultEntity registerResultEntity = userRepository.register(registerEntity);
        // 注册成功 - 登录
        if (registerResultEntity.isSuccess()) {
            // SaToken 登录发放 token
            StpUtil.login(registerEntity.getUserId());
            // 返回结果
            return LoginResultEntity.builder()
                    .isSuccess(true)
                    .message(registerResultEntity.getMessage())
                    .token(StpUtil.getTokenValue())
                    .timeout(StpUtil.getTokenTimeout())
                    .build();
        }
        return LoginResultEntity.builder()
                .isSuccess(false)
                .message(registerResultEntity.getMessage())
                .build();
    }

    @Override
    public LoginResultEntity loginByAcc(LoginByAccEntity loginByAccEntity) {
        // 密码校验成功 - 登录
        if (userRepository.loginByAcc(loginByAccEntity)) {
            // SaToken 登录发放 token
            StpUtil.login(loginByAccEntity.getUserId());
            // 返回结果
            return LoginResultEntity.builder()
                    .isSuccess(true)
                    .token(StpUtil.getTokenValue())
                    .timeout(StpUtil.getTokenTimeout())
                    .build();
        }
        return LoginResultEntity.builder()
                .isSuccess(false)
                .build();
    }
}
