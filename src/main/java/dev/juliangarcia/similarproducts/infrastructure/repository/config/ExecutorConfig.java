package dev.juliangarcia.similarproducts.infrastructure.repository.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import dev.juliangarcia.similarproducts.application.provider.TaskExecutorProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorConfig {

  @Bean
  public TaskExecutorProvider taskExecutorProvider() {
    final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    return () -> executor;
  }

}
