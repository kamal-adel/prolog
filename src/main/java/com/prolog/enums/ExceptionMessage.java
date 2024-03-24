package com.prolog.enums;

public enum ExceptionMessage {
  UTILITY_CLASS_CANNOT_BE_INSTANTIATED("This is a utility class and cannot be instantiated");

  public final String message;

  ExceptionMessage(String message) {
    this.message = message;
  }

}
