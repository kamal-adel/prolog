package com.prolog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Body {

  @JsonProperty("name")
  private String name;

  @JsonProperty("password")
  private String password;
}
