package com.example.springutil.interceptor;

import com.example.springutil.api.RequestContext;
import com.example.springutil.api.RequestContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.CustomLog;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@CustomLog
public class RequestInterceptor implements HandlerInterceptor {
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    log.info(
        "RequestInterceptor preHandle method called for request: {} {}",
        request.getMethod(),
        request.getRequestURI());
    String uuid = UUID.randomUUID().toString();
    String sessionID = request.getSession().getId();
    RequestContext requestContext =
        RequestContext.requestContextBuilder()
            .sessionId(sessionID)
            .customerIpAddress(request.getRemoteAddr())
            .messageId(uuid)
            .build();
    RequestContextHolder.setContext(requestContext);
    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws Exception {
    RequestContextHolder.clearContext();
    log.info(
        " after completion RequestInterceptor afterCompletion method called for request: {} {}",
        request.getMethod(),
        request.getRequestURI());
  }
}
