package qwen.chat.platform.trigger.http;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import qwen.chat.platform.api.LoginService;
import qwen.chat.platform.api.dto.*;
import qwen.chat.platform.api.response.Response;
import qwen.chat.platform.domain.login.UserService;
import qwen.chat.platform.domain.login.model.entity.LoginByAccEntity;
import qwen.chat.platform.domain.login.model.entity.LoginResultEntity;
import qwen.chat.platform.domain.login.model.entity.RegisterEntity;
import qwen.chat.platform.domain.login.model.valobj.CheckIsLoginEnum;
import qwen.chat.platform.domain.login.model.valobj.LogOutStatusEnum;
import qwen.chat.platform.domain.login.model.valobj.LoginStatusEnum;
import qwen.chat.platform.domain.login.model.valobj.RegisterStatusEnum;

import javax.annotation.Resource;

@RestController
@RequestMapping("/login")
@CrossOrigin
@Slf4j
public class LoginController implements LoginService {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    @Override
    public Response<LoginResponseDTO> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        // 参数
        String password = registerRequestDTO.getPassword();
        String confirmPassword = registerRequestDTO.getConfirmPassword();
        String userId = registerRequestDTO.getUserId();
        // 参数校验
        if (userId == null || password == null) {
            return Response.<LoginResponseDTO>builder()
                    .code(String.valueOf(RegisterStatusEnum.NULL_ACCOUNT.getCode()))
                    .info(userId == null ? RegisterStatusEnum.NULL_ACCOUNT.getInfo() : RegisterStatusEnum.NULL_PASSWORD.getInfo())
                    .build();
        }
        if (!password.equals(confirmPassword)) {
            return Response.<LoginResponseDTO>builder()
                    .code(String.valueOf(RegisterStatusEnum.INCONSISTENT_PASSWORD.getCode()))
                    .info(RegisterStatusEnum.INCONSISTENT_PASSWORD.getInfo())
                    .build();
        }
        // 构造参数
        RegisterEntity registerEntity = RegisterEntity.builder()
                .userId(userId)
                .password(password)
                .build();
        // 注册
        LoginResultEntity loginResultEntity = userService.register(registerEntity);
        // 成功
        if (loginResultEntity.isSuccess()) {
            return Response.<LoginResponseDTO>builder()
                    .code(String.valueOf(RegisterStatusEnum.SUCCESS.getCode()))
                    .info(RegisterStatusEnum.SUCCESS.getInfo())
                    .data(LoginResponseDTO.builder()
                            .token(loginResultEntity.getToken())
                            .timeout(loginResultEntity.getTimeout())
                            .build())
                    .build();
        }
        // 失败
        return Response.<LoginResponseDTO>builder()
                .code(String.valueOf(RegisterStatusEnum.FAILED.getCode()))
                .info(loginResultEntity.getMessage())
                .build();
    }

    @PostMapping("/by_acc")
    @Override
    public Response<LoginResponseDTO> loginByAcc(@RequestBody LoginByAccRequestDTO loginByAccRequestDTO) {
        // 参数
        String password = loginByAccRequestDTO.getPassword();
        String userId = loginByAccRequestDTO.getUserId();
        // 参数非空校验
        if (userId == null || password == null) {
            return Response.<LoginResponseDTO>builder()
                    .code(String.valueOf(LoginStatusEnum.NULL_ACCOUNT.getCode()))
                    .info(userId == null ? LoginStatusEnum.NULL_ACCOUNT.getInfo() : LoginStatusEnum.NULL_PASSWORD.getInfo())
                    .build();
        }
        // 构造参数
        LoginByAccEntity loginByAccEntity = LoginByAccEntity.builder()
                .userId(userId)
                .password(password)
                .build();
        // 校验
        LoginResultEntity loginResultEntity = userService.loginByAcc(loginByAccEntity);
        // 成功
        if (loginResultEntity.isSuccess()) {
            return Response.<LoginResponseDTO>builder()
                    .code(String.valueOf(LoginStatusEnum.SUCCESS.getCode()))
                    .info(LoginStatusEnum.SUCCESS.getInfo())
                    .data(LoginResponseDTO.builder()
                            .token(loginResultEntity.getToken())
                            .timeout(loginResultEntity.getTimeout())
                            .build())
                    .build();
        }
        // 失败
        return Response.<LoginResponseDTO>builder()
                .code(String.valueOf(LoginStatusEnum.FAILED.getCode()))
                .info(LoginStatusEnum.FAILED.getInfo())
                .build();
    }

    @PostMapping("/by_vc")
    @Override
    public Response<LoginResponseDTO> loginByVC(@RequestBody LoginByVCRequestDTO loginByVCRequestDTO) {
        return null;
    }

    @PostMapping("/logout")
    @SaCheckLogin
    @Override
    public Response logout(@RequestBody LogOutRequestDTO logOutRequestDTO) {
        // 参数
        String userId = logOutRequestDTO.getUserId();
        // 参数非空校验
        if (userId == null) {
            return Response.builder()
                    .code(String.valueOf(LogOutStatusEnum.NULL_ID.getCode()))
                    .info(LogOutStatusEnum.NULL_ID.getInfo())
                    .build();
        }
        // 是否为同一个用户
        String curId = StpUtil.getLoginIdAsString();
        if (!userId.equals(curId)) {
            return Response.builder()
                    .code(String.valueOf(LogOutStatusEnum.OTHER_ID.getCode()))
                    .info(LogOutStatusEnum.OTHER_ID.getInfo())
                    .build();
        }
        // 退出登录
        return Response.builder()
                .code(String.valueOf(LogOutStatusEnum.SUCCESS.getCode()))
                .info(LogOutStatusEnum.SUCCESS.getInfo())
                .build();
    }

    @Override
    public Response checkIsLogin(CheckIsLoginDTO checkIsLoginDTO) {
        // 参数
        String userId = checkIsLoginDTO.getUserId();
        // 参数非空校验
        if (userId == null) {
            return Response.builder()
                    .code(String.valueOf(CheckIsLoginEnum.NULL_ID.getCode()))
                    .info(CheckIsLoginEnum.NULL_ID.getInfo())
                    .build();
        }
        // 是否为同一个用户
        String curId = StpUtil.getLoginIdAsString();
        if (!userId.equals(curId)) {
            return Response.builder()
                    .code(String.valueOf(CheckIsLoginEnum.OTHER_ID.getCode()))
                    .info(CheckIsLoginEnum.OTHER_ID.getInfo())
                    .build();
        }
        // 校验
        return Response.builder()
                .code(String.valueOf(CheckIsLoginEnum.SUCCESS.getCode()))
                .info(CheckIsLoginEnum.SUCCESS.getInfo())
                .data(StpUtil.isLogin(userId))
                .build();
    }

}

