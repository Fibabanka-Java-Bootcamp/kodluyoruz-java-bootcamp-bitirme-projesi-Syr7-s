package org.kodluyoruz.mybank.config;

import org.kodluyoruz.mybank.interceptors.ResponseTimeLogger;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final ResponseTimeLogger responseTimeLogger;

    public WebConfig(ResponseTimeLogger responseTimeLogger) {
        this.responseTimeLogger = responseTimeLogger;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(responseTimeLogger)
                .excludePathPatterns("/api/book/*");
    }

}
