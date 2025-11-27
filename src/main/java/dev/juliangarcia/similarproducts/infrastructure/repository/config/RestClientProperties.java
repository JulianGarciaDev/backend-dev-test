package dev.juliangarcia.similarproducts.infrastructure.repository.config;

import java.time.Duration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "rest-client")
public class RestClientProperties {
  private Duration connectTimeout;

  private Duration readTimeout;

  private String productBaseUrl;
}
