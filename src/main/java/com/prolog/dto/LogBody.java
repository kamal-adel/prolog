package com.prolog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Data;
import lombok.NonNull;

@Data
public class LogBody {

  @JsonProperty("logPointState")
  private String logPointState;

  @JsonProperty("className")
  private String className;

  @JsonProperty("methodName")
  private String methodName;

  @JsonProperty("arguments")
  private Object[] args;

  @JsonProperty("queryParam")
  private String queryParam;

  @JsonProperty("requestURI")
  private String requestURI;

  @JsonProperty("logLevel")
  private String logLevel;

  @JsonProperty("serviceName")
  private String serviceName;

  @JsonProperty("traceId")
  private String traceId;

  @JsonProperty("requestHeaders")
  private Map<String, String> requestHeaders;

  @JsonProperty("responseBody")
  private Object responseBody;

  @JsonProperty("responseStatusCode")
  private Integer responseStatusCode;

  @JsonProperty("responseError")
  private String responseError;

  @JsonProperty("requestSizeBytes")
  private Double requestSizeBytes;

  @JsonProperty("responseSizeBytes")
  private Double responseSizeBytes;

  @JsonProperty("httpMethod")
  private String httpMethod;

  @JsonProperty("executionTimeMs")
  private String executionTimeMs;

  @JsonProperty("timeStamp")
  private String timeStamp;

  @JsonProperty("environment")
  private String environment;

  @JsonProperty("version")
  private String version;

  @JsonProperty("contentType")
  private String contentType;
}
