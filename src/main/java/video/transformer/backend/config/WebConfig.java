package video.transformer.backend.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private JwtInterceptor jwtInterceptor;


    // 配置静态资源映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/video/**")
            .addResourceLocations("file:src/main/resources/static/video/")
            .addResourceLocations("classpath:/static/video/");
    }

    // 配置拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns(
                "/artifacts/uploads/**",
                "/user/login",
                "/auth/login",
                "/auth/register",
                    "/file/image/**"
            );
    }
}