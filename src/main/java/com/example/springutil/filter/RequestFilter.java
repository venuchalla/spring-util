package com.example.springutil.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import java.io.IOException;
import lombok.CustomLog;
import org.springframework.stereotype.Component;

@WebFilter(filterName = "RequestFilter", urlPatterns = "/*")
@CustomLog
@Component
public class RequestFilter implements Filter {
  @Override
  public void doFilter(
      ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {
    // You can add your custom logic here, for example, logging or modifying the request/response
    log.info("RequestFilter: Processing request: {}", servletRequest.getRemoteAddr());
    filterChain.doFilter(servletRequest, servletResponse);
  }
}
