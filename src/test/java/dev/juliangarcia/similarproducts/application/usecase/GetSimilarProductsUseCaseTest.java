package dev.juliangarcia.similarproducts.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import dev.juliangarcia.similarproducts.application.provider.TaskExecutorProvider;
import dev.juliangarcia.similarproducts.domain.entity.ProductDetails;
import dev.juliangarcia.similarproducts.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetSimilarProductsUseCaseTest {

  @Mock
  private ProductRepository productRepository;

  @Mock
  private TaskExecutorProvider executorProvider;

  private GetSimilarProductsUseCase useCase;

  @BeforeEach
  void setUp() {
    this.useCase = new GetSimilarProductsUseCase(this.productRepository, this.executorProvider);
  }

  @Test
  void shouldReturnSimilarProductsSuccessfully() {
    final String productId = "1";
    final List<String> similarIds = Arrays.asList("2", "3", "4");
    final ProductDetails product2 = this.createProductDetails("2", "Product 2", 10.0, true);
    final ProductDetails product3 = this.createProductDetails("3", "Product 3", 20.0, true);
    final ProductDetails product4 = this.createProductDetails("4", "Product 4", 30.0, true);

    final Executor mockExecutor = this.mockExecutor();
    when(this.productRepository.findSimilarProductsById(productId)).thenReturn(similarIds);
    when(this.productRepository.findProductDetailsById("2")).thenReturn(product2);
    when(this.productRepository.findProductDetailsById("3")).thenReturn(product3);
    when(this.productRepository.findProductDetailsById("4")).thenReturn(product4);

    final List<ProductDetails> result = this.useCase.execute(productId);

    assertThat(result).hasSize(3).containsExactly(product2, product3, product4);
    verify(this.productRepository).findSimilarProductsById(productId);
    verify(this.productRepository, times(3)).findProductDetailsById(anyString());
    verifyNoMoreInteractions(this.productRepository);
    verify(mockExecutor, times(3)).execute(any(Runnable.class));
  }

  @Test
  void shouldReturnEmptyListWhenNoSimilarProducts() {
    final String productId = "1";
    when(this.productRepository.findSimilarProductsById(productId)).thenReturn(List.of());

    final List<ProductDetails> result = this.useCase.execute(productId);

    assertThat(result).isEmpty();
    verify(this.productRepository).findSimilarProductsById(productId);
    verify(this.productRepository, never()).findProductDetailsById(anyString());
    verifyNoMoreInteractions(this.productRepository);
  }

  @Test
  void shouldRemoveDuplicateSimilarProductIds() {
    final String productId = "1";
    final List<String> similarIdsWithDuplicates = Arrays.asList("2", "3", "2", "4", "3");
    final ProductDetails product2 = this.createProductDetails("2", "Product 2", 10.0, true);
    final ProductDetails product3 = this.createProductDetails("3", "Product 3", 20.0, false);
    final ProductDetails product4 = this.createProductDetails("4", "Product 4", 30.0, true);

    final Executor mockExecutor = this.mockExecutor();
    when(this.productRepository.findSimilarProductsById(productId)).thenReturn(similarIdsWithDuplicates);
    when(this.productRepository.findProductDetailsById("2")).thenReturn(product2);
    when(this.productRepository.findProductDetailsById("3")).thenReturn(product3);
    when(this.productRepository.findProductDetailsById("4")).thenReturn(product4);

    final List<ProductDetails> result = this.useCase.execute(productId);

    assertThat(result).hasSize(3);
    verify(this.productRepository).findProductDetailsById("2");
    verify(this.productRepository).findProductDetailsById("3");
    verify(this.productRepository).findProductDetailsById("4");
    verify(this.productRepository, times(3)).findProductDetailsById(anyString());
    verifyNoMoreInteractions(this.productRepository);
    verify(mockExecutor, times(3)).execute(any(Runnable.class));
  }

  private ProductDetails createProductDetails(String id, String name, double price, boolean availability) {
    final ProductDetails productDetails = new ProductDetails();
    productDetails.setProductId(id);
    productDetails.setName(name);
    productDetails.setPrice(price);
    productDetails.setAvailability(availability);
    return productDetails;
  }

  private Executor mockExecutor() {
    final Executor mockExecutor = mock(Executor.class);
    when(this.executorProvider.getExecutor()).thenReturn(mockExecutor);

    doAnswer(invocation -> {
      final Runnable task = invocation.getArgument(0);
      CompletableFuture.runAsync(task);
      return null;
    }).when(mockExecutor).execute(any(Runnable.class));
    return mockExecutor;
  }

}
