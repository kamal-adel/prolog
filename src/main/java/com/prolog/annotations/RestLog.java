package com.prolog.annotations;

import static com.prolog.enums.LogLevel.INFO;

import com.prolog.enums.LogLevel;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestLog {
  boolean logOnEntry() default true;
  boolean logOnExit() default true;
  LogLevel level() default INFO;
  boolean hideSensitiveData() default false;
}