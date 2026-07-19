package com.example.springutil.logs;

public interface ICustomLogger {
  boolean isInfoEnabled();

  void info(String message, Object... args);

  boolean isErrorEnabled();

  void error(String message, Object... args);

  boolean isWarnEnabled();

  void warn(String message, Object... args);

  boolean isTraceEnabled();

  void trace(String message, Object... args);

  boolean isDebugEnabled();

  void debug(String message, Object... args);
}
