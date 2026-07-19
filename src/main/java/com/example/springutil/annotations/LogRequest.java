package com.example.springutil.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface LogRequest {
  boolean enforceRequestWrapper() default true;

  boolean enforceResponseWrapper() default true;

  LogLevel logParameters() default LogLevel.FULL;

  LogLevel logResponse() default LogLevel.NONE;

  enum LogLevel {
    NONE,
    FULL,
    BRIEF
  }
}
