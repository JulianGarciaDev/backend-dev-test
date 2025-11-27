package dev.juliangarcia.similarproducts.infrastructure.repository.config;

import io.netty.channel.ChannelOption;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@EnableConfigurationProperties(RestClientProperties.class)
public class RestClientConfig {

  private final RestClientProperties properties;

  public RestClientConfig(RestClientProperties properties) {
    this.properties = properties;
  }

  @Bean
  public WebClient productRestClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(this.properties.getProductBaseUrl())
        .clientConnector(new ReactorClientHttpConnector(this.createHttpClient()))
        .build();
  }

  private HttpClient createHttpClient() {
    return HttpClient.create()
        .responseTimeout(this.properties.getReadTimeout())
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) this.properties.getConnectTimeout().toMillis());
  }
}
