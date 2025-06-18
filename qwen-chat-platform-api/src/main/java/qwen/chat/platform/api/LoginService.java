package qwen.chat.platform.api;

import qwen.chat.platform.api.dto.*;
import qwen.chat.platform.api.response.Response;

public interface LoginService {

    /**
     * 注册并登录
     * @param registerRequestDTO
     * @return
     */
    Response<LoginResponseDTO> register(RegisterRequestDTO registerRequestDTO);

    /**
     * 使用账密登录
     * @param loginByAccRequestDTO
     * @return
     */
    Response<LoginResponseDTO> loginByAcc(LoginByAccRequestDTO loginByAccRequestDTO);

    /**
     * 使用验证码登录
     * @param loginByVCRequestDTO
     * @return
     */
    Response<LoginResponseDTO> loginByVC(LoginByVCRequestDTO loginByVCRequestDTO);

    /**
     * 退出登录
     * @param logOutRequestDTO
     * @return
     */
    Response logout(LogOutRequestDTO logOutRequestDTO);

    /**
     * 校验是否登录
     * @param checkIsLoginDTO
     * @return
     */
    Response checkIsLogin(CheckIsLoginDTO checkIsLoginDTO);
}
