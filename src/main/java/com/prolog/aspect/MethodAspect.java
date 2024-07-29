package com.prolog.aspect;

import static com.prolog.config.TraceIdFilter.generateTraceId;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prolog.annotations.MethodLogger;
import com.prolog.config.LogConfiguration;
import com.prolog.dto.LogBody;
import com.prolog.logger.LoggerFactory;
import com.prolog.masking.MaskingUtil;
import com.prolog.utils.HttpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
@Aspect
@Component
public class MethodAspect extends BaseAspect{
  private final LogConfiguration logConfiguration;

  private Logger logger;
  private String traceId;

  MethodAspect(LogConfiguration logConfiguration) {
    super(new ObjectMapper(), logConfiguration);
    this.logConfiguration = logConfiguration;

  }

  @Around("@annotation(methodLogger)")
  public Object logAroundClass(ProceedingJoinPoint joinPoint, MethodLogger methodLogger) {
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return applyLogExecutionAnnotation(joinPoint, methodLogger);
  }

  @SneakyThrows
  public Object applyLogExecutionAnnotation(ProceedingJoinPoint joinPoint, MethodLogger logAnnotation) {

    traceId = generateTraceId();
    LogBody logBody = new LogBody();
    logOnEntry(joinPoint, logAnnotation, logBody);
    Object result = joinPoint.proceed();
    logOnExit(joinPoint, logAnnotation, result, logBody);
    return result;
  }


  private void logOnEntry(ProceedingJoinPoint joinPoint, MethodLogger logAnnotation, LogBody logBody) {
    logger = LoggerFactory.getInstance(joinPoint.getClass());
    if (logAnnotation.logOnEntry()) {
      ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      HttpServletRequest request = null;
      if (attributes != null) {
        request = attributes.getRequest();
      }
      if (request != null) {
        constructLogBody(joinPoint, request, null , logBody);
        setMethodLoggerAnnotationAttributes(joinPoint, null, logBody, logAnnotation);
        setConfigurationAttributes(logBody, traceId);
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

  private void logOnExit(ProceedingJoinPoint joinPoint, MethodLogger logAnnotation, Object result, LogBody logBody) {
    logger = LoggerFactory.getInstance(joinPoint.getClass());
    if (logAnnotation.logOnExit()) {
      ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      HttpServletResponse response = null;
      if (attributes != null) {
        response = attributes.getResponse();
      }
      if (response != null) {
        constructLogBody(joinPoint, response, result , logBody);
        setMethodLoggerAnnotationAttributes(joinPoint, result, logBody, logAnnotation);
        logBody(logBody, logAnnotation, logger);
      }
    }
  }
  protected static void setMethodLoggerAnnotationAttributes(ProceedingJoinPoint joinPoint, Object result, LogBody logBody, MethodLogger annotation) {
    logBody.setLogLevel(annotation.level().name());
    if (!annotation.hideRequestBody()) {
      logBody.setArgs(joinPoint.getArgs());
    }
    if (!annotation.hideResponseBody()) {
      logBody.setResponseBody(result);
    }
  }
  @SneakyThrows
  private void logBody(LogBody logBodyObject, MethodLogger classLogger, Logger logger) {
    String stringifyLogBody = null;
    JsonNode jsonNode = null;
    if (classLogger.hideResponseBodySensitiveData()) {
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
}
