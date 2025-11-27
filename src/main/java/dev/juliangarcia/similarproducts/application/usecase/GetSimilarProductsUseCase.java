package dev.juliangarcia.similarproducts.application.usecase;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import dev.juliangarcia.similarproducts.application.provider.TaskExecutorProvider;
import dev.juliangarcia.similarproducts.domain.entity.ProductDetails;
import dev.juliangarcia.similarproducts.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class GetSimilarProductsUseCase {

  private final ProductRepository productRepository;

  private final TaskExecutorProvider executorProvider;

  public GetSimilarProductsUseCase(ProductRepository productRepository, TaskExecutorProvider executorProvider) {
    this.productRepository = productRepository;
    this.executorProvider = executorProvider;
  }

  public List<ProductDetails> execute(String productId) {
    final List<String> similarProductIds = this.getDistinctSimilarProductIds(productId);
    return this.fetchProductDetailsConcurrently(similarProductIds);
  }

  private List<String> getDistinctSimilarProductIds(final String productId) {
    final List<String> similarProductIds = this.productRepository.findSimilarProductsById(productId);
    return similarProductIds.stream().distinct().toList();
  }

  private List<ProductDetails> fetchProductDetailsConcurrently(final List<String> similarProductIds) {
    final List<CompletableFuture<ProductDetails>> futures = similarProductIds
        .stream()
        .map(id -> CompletableFuture.supplyAsync(
            () -> this.productRepository.findProductDetailsById(id), this.executorProvider.getExecutor()))
        .toList();

    return futures.stream().map(CompletableFuture::join).toList();
  }

}
