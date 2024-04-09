package com.prolog.aspect;

import static com.prolog.config.TraceIdFilter.getTraceIdAttribute;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prolog.annotations.ServiceLog;
import com.prolog.config.LogConfiguration;
import com.prolog.dto.LogBody;
import com.prolog.dto.LogRequest;
import com.prolog.dto.LogResponse;
import com.prolog.enums.LogLevel;
import com.prolog.masking.MaskingPatternLayout;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLogAspect {

  private static final Logger logger = (Logger) LoggerFactory.getLogger(ServiceLogAspect.class);
  private final LogConfiguration logConfiguration;

  private Exception exception = null;
  private LogLevel level;


  public ServiceLogAspect(LogConfiguration logConfiguration) {
    this.logConfiguration = logConfiguration;
  }

  @Around("execution(* *(..)) && @within(serviceLog) && !@within(org.springframework.web.bind.annotation.RestController)")
  public Object logAroundService(ProceedingJoinPoint joinPoint, ServiceLog serviceLog) {
    return applyLogExecutionAnnotation(joinPoint, serviceLog);
  }

  @SneakyThrows
  public Object applyLogExecutionAnnotation(ProceedingJoinPoint joinPoint, ServiceLog logAnnotation) {
    level = logAnnotation.level();
    exception = null;
    MaskingPatternLayout.enableMaskingIfAnnotated(joinPoint.getTarget());
    logOnEntry(joinPoint, logAnnotation);
    Object result = proceedJointPoint(joinPoint);
    logOnExit(joinPoint, logAnnotation, result);
    return result;
  }

  private Object proceedJointPoint(ProceedingJoinPoint joinPoint) throws Throwable {
    Object result = null;
    try {
      result = joinPoint.proceed();
    } catch (Exception e) {
      exception = e;
    }
    return result;
  }

  private void logOnEntry(ProceedingJoinPoint joinPoint, ServiceLog logAnnotation) throws JsonProcessingException {
    if (logAnnotation.logOnEntry()) {
      LogRequest logRequest = new LogRequest();
      LogBody logBodyObject = constructLogBody(logRequest, joinPoint);
      logBodyObject.setLogPointState(logConfiguration.getRequestIdentifier());
      logBody(logBodyObject);
    }
  }

  private void logOnExit(ProceedingJoinPoint joinPoint, ServiceLog logAnnotation, Object result) throws Exception {
    if (logAnnotation.logOnExit()) {
      LogResponse logResponse = new LogResponse();
      LogBody logBody = constructLogBody(logResponse, joinPoint);
      ((LogResponse) logBody).setResponseBody(result);
      logBody.setLogPointState(logConfiguration.getResponseIdentifier());
      if (exception != null) {
        ((LogResponse) logBody).setResponseError(exception.getMessage());
        level = LogLevel.ERROR;
        logBody.setLogLevel(LogLevel.ERROR.name());
      }
      logBody(logResponse);
      if(exception!=null) {
        throw exception;
      }
    }
  }

  private void logBody(LogBody logBody) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    String stringifyLogBody;
    if (logConfiguration.isPrettyLogFormat()) {
      stringifyLogBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logBody);
    } else {
      stringifyLogBody = objectMapper.writeValueAsString(logBody);
    }
    logBasedOnLevel(stringifyLogBody);
  }

  @SneakyThrows
  private LogBody constructLogBody(LogBody logBody, ProceedingJoinPoint joinPoint) {
    logBody.setClassName(joinPoint.getSignature().getDeclaringType().getSimpleName());
    logBody.setMethodName(joinPoint.getSignature().getName());
    logBody.setLogLevel(level.name());
    logBody.setServiceName(logConfiguration.getServiceName());
    logBody.setTraceId(getTraceIdAttribute());
    return logBody;
  }

  private void logBasedOnLevel(String loggingBody) {
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

}
