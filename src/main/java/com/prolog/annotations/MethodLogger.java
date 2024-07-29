package com.prolog.annotations;

import static com.prolog.enums.LogLevel.INFO;

import com.prolog.enums.LogLevel;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodLogger {

  boolean logOnEntry() default true;

  boolean logOnExit() default true;

  LogLevel level() default INFO;

  boolean hideHeadersSensitiveData() default false;

  boolean hideRequestBodySensitiveData() default false;

  boolean hideResponseBodySensitiveData() default false;

  boolean hideRequestBody() default false;

  boolean hideResponseBody() default false;

  String[] sensitiveAttributes() default {"password", "token", "x-api-key"};
}
