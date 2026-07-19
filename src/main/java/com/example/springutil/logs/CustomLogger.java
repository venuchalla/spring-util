package com.example.springutil.logs;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.spi.LoggingEventBuilder;

@RequiredArgsConstructor
public class CustomLogger implements ICustomLogger {
  private final Logger logger;

  @Override
  public boolean isInfoEnabled() {
    return logger.isInfoEnabled();
  }

  @Override
  public void info(String message, Object... args) {
    append(logger.atInfo(), message, args);
  }

  @Override
  public boolean isErrorEnabled() {
    return logger.isErrorEnabled();
  }

  @Override
  public void error(String message, Object... args) {
    append(logger.atError(), message, args);
  }

  @Override
  public boolean isWarnEnabled() {
    return logger.isWarnEnabled();
  }

  @Override
  public void warn(String message, Object... args) {
    append(logger.atWarn(), message, args);
  }

  @Override
  public boolean isTraceEnabled() {
    return logger.isTraceEnabled();
  }

  @Override
  public void trace(String message, Object... args) {
    append(logger.atTrace(), message, args);
  }

  @Override
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  @Override
  public void debug(String message, Object... args) {
    append(logger.atDebug(), message, args);
  }

  private void append(LoggingEventBuilder builder, String message, Object... args) {
    builder.setMessage(message);

    //        if (args != null) {
    //            int length = args.length;
    //
    //            if (length > 0 && args[length - 1] instanceof Throwable throwable) {
    //                builder.setCause(throwable);
    //                length--;
    //            }

    //            for (int i = 0; i < length; i++) {
    //                builder.addArgument(args[i]);
    //            }
    // }

    builder.log("custom" + message, args);
  }
}
