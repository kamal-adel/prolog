package com.prolog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

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
}
