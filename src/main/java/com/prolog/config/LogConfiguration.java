package com.prolog.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "prolog.logging")
@Validated
@Data
public class LogConfiguration {

  @NotNull
  @NotBlank
  private String serviceName;

  private String environmentName;

  @NotNull
  @NotBlank
  private String responseIdentifier;

  @NotNull
  @NotBlank
  private String requestIdentifier;

  private boolean prettyLogFormat;

  private boolean enableMasking;
}
