package com.prolog.logger;

import org.slf4j.Logger;

public class LoggerFactory {

  private static Logger logger;

  private LoggerFactory() {
  }

  public static synchronized Logger getInstance(Class<?> clazz) {
    if (logger == null) {
      logger = org.slf4j.LoggerFactory.getLogger(clazz);
    }
    return logger;
  }

}
