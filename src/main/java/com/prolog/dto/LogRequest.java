package com.prolog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LogRequest extends LogBody {

  @JsonProperty("requestHeaders")
  private Map<String, String> requestHeaders;

}
