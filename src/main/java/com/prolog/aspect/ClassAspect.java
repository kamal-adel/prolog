package com.prolog.aspect;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prolog.annotations.ClassLogger;
import com.prolog.config.LogConfiguration;
import com.prolog.config.TraceIdFilter;
import com.prolog.dto.LogBody;
import com.prolog.logger.LoggerFactory;
import com.prolog.masking.MaskingUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class ClassAspect extends BaseAspect {


  private Logger logger;

  ClassAspect(LogConfiguration logConfiguration, ObjectMapper objectMapper) {
    super(objectMapper, logConfiguration);
  }

  @Around("execution(* *(..)) && @within(classLogger)")
  public Object logAroundClass(ProceedingJoinPoint joinPoint, ClassLogger classLogger) {
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return applyLogExecutionAnnotation(joinPoint, classLogger);
  }

  @SneakyThrows
  public Object applyLogExecutionAnnotation(ProceedingJoinPoint joinPoint, ClassLogger logAnnotation) {
    LogBody logBody = new LogBody();

    logOnEntry(joinPoint, logAnnotation, logBody);

    Object result = joinPoint.proceed();

    logOnExit(joinPoint, logAnnotation, result, logBody);
    return result;
  }


  private void logOnEntry(ProceedingJoinPoint joinPoint, ClassLogger logAnnotation, LogBody logBody) throws NoSuchMethodException {
    logger = LoggerFactory.getInstance(joinPoint.getClass());
    if (logAnnotation.logOnEntry()) {
      ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      HttpServletRequest request = null;
      if (attributes != null) {
        request = attributes.getRequest();
      }
      if (request != null) {
        constructLogBody(joinPoint, request, null , logBody);
        setClassLoggerAnnotationAttributes(joinPoint, null, logBody, logAnnotation);
        setConfigurationAttributes(logBody, TraceIdFilter.getTraceIdAttribute());
        logBody(logBody, logAnnotation, logger);
      }
    }
  }

  private void setConfigurationAttributes(LogBody logBody, String traceId) {
    logBody.setTraceId(traceId);
    logBody.setServiceName(logConfiguration.getServiceName());
    logBody.setEnvironment(logConfiguration.getEnvironmentName());
    logBody.setVersion(logConfiguration.getVersion());
  }

  private void logOnExit(ProceedingJoinPoint joinPoint, ClassLogger logAnnotation, Object result, LogBody logBody) throws NoSuchMethodException {
    logger = LoggerFactory.getInstance(joinPoint.getClass());
    if (logAnnotation.logOnExit()) {
      ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      HttpServletResponse response = null;
      if (attributes != null) {
        response = attributes.getResponse();
      }
      if (response != null) {
        constructLogBody(joinPoint, response, result, logBody);
        setClassLoggerAnnotationAttributes(joinPoint, null, logBody, logAnnotation);
        logBody(logBody, logAnnotation, logger);
      }
    }
  }



  @SneakyThrows
  private void logBody(LogBody logBodyObject, ClassLogger classLogger, Logger logger) {
    String stringifyLogBody = null;
    JsonNode jsonNode = null;
    if (classLogger.hideHeadersSensitiveData()) {
      stringifyLogBody = objectMapper.writeValueAsString(logBodyObject);
      jsonNode = MaskingUtil.maskSensitiveData(stringifyLogBody, "************", classLogger.sensitiveAttributes());
    }
    if (logConfiguration.isPrettyLogFormat()) {
      stringifyLogBody = getPrettyStringifyLogBody(logBodyObject, jsonNode);
    } else {
      stringifyLogBody = getStringifyLogBody(logBodyObject, jsonNode);
    }
    logBasedOnLevel(stringifyLogBody, classLogger.level(), logger);
  }

  protected static void setClassLoggerAnnotationAttributes(ProceedingJoinPoint joinPoint, Object result, LogBody logBody, ClassLogger annotation) {
    logBody.setLogLevel(annotation.level().name());
    if (!annotation.hideRequestBody()) {
      logBody.setArgs(joinPoint.getArgs());
    }
    if (!annotation.hideResponseBody()) {
      logBody.setResponseBody(result);
    }
  }
}
