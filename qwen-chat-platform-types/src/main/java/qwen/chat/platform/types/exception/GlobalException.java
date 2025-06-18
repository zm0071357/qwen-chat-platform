package qwen.chat.platform.types.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler
    public Response handlerException(Exception e) {
        e.printStackTrace();
        return Response.builder()
                .code(e instanceof cn.dev33.satoken.exception.SaTokenException ? 403 : 1)
                .info(e.getMessage())
                .build();
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private Integer code;
        private String info;
    }

}
