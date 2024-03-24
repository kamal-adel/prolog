package com.prolog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LogResponse extends LogBody {

  @JsonProperty("responseBody")
  private Object responseBody;

  @JsonProperty("responseStatusCode")
  private Integer responseStatusCode;

  @JsonProperty("responseError")
  private String responseError;

}
