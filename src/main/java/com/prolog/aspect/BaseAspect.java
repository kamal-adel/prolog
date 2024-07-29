package com.prolog.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prolog.config.LogConfiguration;
import com.prolog.dto.LogBody;
import com.prolog.enums.LogLevel;
import com.prolog.logger.LoggerFactory;
import com.prolog.utils.HttpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;

public class BaseAspect {

  protected final ObjectMapper objectMapper;
  protected final LogConfiguration logConfiguration;

  public BaseAspect(ObjectMapper objectMapper, LogConfiguration logConfiguration) {
    this.objectMapper = objectMapper;
    this.logConfiguration = logConfiguration;
  }

  protected void logBasedOnLevel(String loggingBody, LogLevel level, Logger logger) {
    switch (level) {
      case INFO:
        logger.info(loggingBody);
        break;
      case DEBUG:
        logger.debug(loggingBody);
        break;
      case ERROR:
        logger.error(loggingBody);
        break;
      default:
        break;
    }
  }

  protected String getStringifyLogBody(LogBody logBodyObject, JsonNode jsonNode) {
    String stringifyLogBody;
    if (jsonNode != null) {
      stringifyLogBody = getStringifyLogBody(jsonNode);
    } else {
      stringifyLogBody = getStringifyLogBody(logBodyObject);
    }
    return stringifyLogBody;
  }

  protected String getPrettyStringifyLogBody(LogBody logBodyObject, JsonNode jsonNode) {
    String stringifyLogBody;
    if (jsonNode != null) {
      stringifyLogBody = getPrettyStringifyLogBody(jsonNode);
    } else {
      stringifyLogBody = getPrettyStringifyLogBody(logBodyObject);
    }
    return stringifyLogBody;
  }

  protected String getStringifyLogBody(Object object) {
    String stringifyLogBody = null;
    try {
      stringifyLogBody = objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      LoggerFactory.getInstance(object.getClass()).error("ERROR OCCURRED WHILE LOGGING BODY.... :{}", e.getMessage());
    }
    return stringifyLogBody;
  }

  protected String getPrettyStringifyLogBody(Object object) {
    String stringifyLogBody = null;
    try {
      stringifyLogBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      LoggerFactory.getInstance(object.getClass()).error("ERROR OCCURRED WHILE LOGGING BODY.... :{}", e.getMessage());
    }
    return stringifyLogBody;
  }
  public static <T extends Annotation> T getAnnotation(Method method, Class<T> annotationClass) {
    if (method.isAnnotationPresent(annotationClass)) {
      return method.getAnnotation(annotationClass);
    }
    return null;
  }

  protected void constructLogBody(ProceedingJoinPoint joinPoint, Object object, Object result, LogBody logBody) {
    logBody.setTimeStamp(String.valueOf(System.currentTimeMillis()));
    logBody.setMethodName(joinPoint.getSignature().getName());
    logBody.setClassName(joinPoint.getTarget().getClass().getName());
    if (object instanceof HttpServletRequest request) {
      logBody.setRequestURI(request.getRequestURI());
      logBody.setRequestHeaders(HttpUtil.extractRequestHeaders(request));
      logBody.setQueryParam(request.getQueryString());
      logBody.setHttpMethod(request.getMethod());
      logBody.setRequestSizeBytes((double) request.getContentLength());
      logBody.setLogPointState(logConfiguration.getRequestIdentifier());
    }
    if (object instanceof HttpServletResponse response) {
      logBody.setContentType(response.getContentType());
      logBody.setResponseStatusCode(response.getStatus());
      logBody.setLogPointState(logConfiguration.getResponseIdentifier());
      logBody.setRequestHeaders(HttpUtil.extractRequestHeaders(null));
      logBody.setArgs(null);
    }
  }


}
