package dev.juliangarcia.similarproducts.application.provider;

import java.util.concurrent.Executor;

public interface TaskExecutorProvider {
  Executor getExecutor();
}
