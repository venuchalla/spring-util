package com.example.springutil.config;

import com.example.springutil.interceptor.RequestInterceptor;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@CustomLog
@Configuration
@RequiredArgsConstructor
public class WebFilterConfig implements WebMvcConfigurer {

  // private final RequestInterceptor requestInterceptor;

  @Bean
  public RequestInterceptor requestInterceptor() {
    return new RequestInterceptor();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    log.info("Adding RequestInterceptor to the registry");
    registry.addInterceptor(requestInterceptor());
  }
}
