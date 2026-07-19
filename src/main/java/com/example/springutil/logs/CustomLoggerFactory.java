package com.example.springutil.logs;

import org.slf4j.LoggerFactory;

public class CustomLoggerFactory {

  public static CustomLogger getLogger(Class<?> clazz) {
    return new CustomLogger(LoggerFactory.getLogger(clazz));
  }
}
