package qwen.chat.platform.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class SaTokenConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/**")     // 拦截全部
                .excludePathPatterns("/login/register")     // 放开注册接口
                .excludePathPatterns("/login/by_acc")   // 放开登录接口
                ;
        log.info("Satoken拦截器装配完成");
    }
}

