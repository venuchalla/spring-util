package com.example.springutil.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.CustomLog;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@CustomLog
@ConditionalOnProperty(name = "loggingReqAndResFilter.enabled", havingValue = "true")
public class RqAndRsLogFilter implements Filter {
  private static final String TRACE_ID = "traceId";

  @Override
  public void doFilter(
      ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {

    ContentCachingRequestWrapper wrappedRequest =
        new ContentCachingRequestWrapper((HttpServletRequest) servletRequest);
    ContentCachingResponseWrapper wrappedResponse =
        new ContentCachingResponseWrapper((HttpServletResponse) servletResponse);
    if (wrappedRequest.getRequestURI().contains("actuator")
        || wrappedRequest.getRequestURI().contains("swagger")
        || wrappedRequest.getRequestURI().contains("api-docs")) {
      /// codedemo/api-docs
      filterChain.doFilter(servletRequest, servletResponse);
      return;
    }
    try {
      String traceId = UUID.randomUUID().toString();
      log.info("RqAndRsLogFilter =======: {} ", traceId);
      MDC.put(TRACE_ID, traceId);
      filterChain.doFilter(wrappedRequest, wrappedResponse);
    } finally {
      String body = new String(wrappedRequest.getContentAsByteArray(), StandardCharsets.UTF_8);
      String rbody = new String(wrappedResponse.getContentAsByteArray(), StandardCharsets.UTF_8);
      log.info("Request uri : {} , trace id {}", wrappedRequest.getRequestURI(), MDC.get(TRACE_ID));
      log.info("Request message :" + body);
      log.info("response :" + rbody);
      wrappedResponse.copyBodyToResponse(); // required
      MDC.remove(TRACE_ID);
    }
  }
}
