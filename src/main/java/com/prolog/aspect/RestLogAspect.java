package com.prolog.aspect;

import static com.prolog.config.TraceIdFilter.getTraceIdAttribute;
import static com.prolog.utils.HttpUtil.extractRequestHeaders;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prolog.annotations.RestLog;
import com.prolog.config.LogConfiguration;
import com.prolog.dto.LogBody;
import com.prolog.dto.LogRequest;
import com.prolog.dto.LogResponse;
import com.prolog.enums.LogLevel;
import com.prolog.masking.MaskingPatternLayout;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class RestLogAspect {

  private Logger logger;
  private final LogConfiguration logConfiguration;
  private Exception exception = null;
  private LogLevel level;


  public RestLogAspect(LogConfiguration logConfiguration) {
    this.logConfiguration = logConfiguration;
  }


  @Around("execution(* *(..)) && @within(logExecution) && @within(org.springframework.web.bind.annotation.RestController)")
  public Object logAroundRestController(ProceedingJoinPoint joinPoint, RestLog logExecution) {
    Class<?> clazz = joinPoint.getTarget().getClass();
    logger = (Logger) LoggerFactory.getLogger(clazz);
    return applyLogExecutionAnnotation(joinPoint, logExecution);
  }

  @SneakyThrows
  public Object applyLogExecutionAnnotation(ProceedingJoinPoint joinPoint, RestLog logExecution) {
    exception = null;
    level = logExecution.level();
    MaskingPatternLayout.enableMaskingIfAnnotated(joinPoint.getTarget());
    logOnEntry(joinPoint, logExecution);
    Object result = proceedJointPoint(joinPoint);
    logOnExit(joinPoint, logExecution, result);
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

  private void logOnExit(ProceedingJoinPoint joinPoint, RestLog restLog, Object response) throws Exception {
    if (restLog.logOnExit()) {
      Object[] args = joinPoint.getArgs();
      LogResponse logResponse = new LogResponse();
      LogBody logBodyObject = constructLogBody(logResponse, joinPoint);
      logBodyObject.setLogPointState(logConfiguration.getResponseIdentifier());
      HttpServletResponse httpServletResponse = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
      HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
      assert httpServletResponse != null;
      ((LogResponse) logBodyObject).setResponseBody(response);
      ((LogResponse) logBodyObject).setResponseStatusCode(httpServletResponse.getStatus());
      logBodyObject.setArgs(args);
      logBodyObject.setQueryParam(httpServletRequest.getQueryString());
      logBodyObject.setRequestURI(httpServletRequest.getRequestURI());
      if (exception != null) {
        ((LogResponse) logBodyObject).setResponseError(exception.getMessage());
        logBodyObject.setLogLevel(LogLevel.ERROR.name());
        level = LogLevel.ERROR;
        ((LogResponse) logBodyObject).setResponseStatusCode(null);
      }
      logBody(logBodyObject);
      if(exception != null) {
        throw exception;
      }
    }
  }

  private void logOnEntry(ProceedingJoinPoint joinPoint, RestLog logExecution) {
    if (logExecution.logOnEntry()) {
      Object[] args = joinPoint.getArgs();
      LogRequest logRequest = new LogRequest();
      LogBody logBodyObject = constructLogBody(logRequest, joinPoint);
      logBodyObject.setLogPointState(logConfiguration.getRequestIdentifier());
      HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
      Map<String, String> requestHeaders = extractRequestHeaders(httpServletRequest);
      ((LogRequest) logBodyObject).setRequestHeaders(requestHeaders);
      logBodyObject.setArgs(args);
      logBodyObject.setQueryParam(httpServletRequest.getQueryString());
      logBodyObject.setRequestURI(httpServletRequest.getRequestURI());
      logBody(logBodyObject);
    }
  }

  @SneakyThrows
  private void logBody(LogBody logBodyObject) {
    ObjectMapper objectMapper = new ObjectMapper();
    String stringifyLogBody;
    if (logConfiguration.isPrettyLogFormat()) {
      stringifyLogBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logBodyObject);
    } else {
      stringifyLogBody = objectMapper.writeValueAsString(logBodyObject);
    }
    logBasedOnLevel(stringifyLogBody);
  }

  private LogBody constructLogBody(LogBody logBody, ProceedingJoinPoint proceedingJoinPoint) {
    logBody.setClassName(proceedingJoinPoint.getSignature().getDeclaringType().getSimpleName());
    logBody.setMethodName(proceedingJoinPoint.getSignature().getName());
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
