package dev.juliangarcia.similarproducts.infrastructure.repository;

import java.util.List;

import dev.juliangarcia.similarproducts.domain.entity.ProductDetails;
import dev.juliangarcia.similarproducts.domain.exception.ProductConnectionException;
import dev.juliangarcia.similarproducts.domain.exception.ProductDetailErrorException;
import dev.juliangarcia.similarproducts.domain.exception.ProductDetailNotFoundException;
import dev.juliangarcia.similarproducts.domain.exception.ProductDetailTimeoutException;
import dev.juliangarcia.similarproducts.domain.repository.ProductRepository;
import dev.juliangarcia.similarproducts.infrastructure.repository.entity.ProductDetailsEntity;
import dev.juliangarcia.similarproducts.infrastructure.repository.entity.SimilarProductsEntity;
import dev.juliangarcia.similarproducts.infrastructure.repository.mapper.RepositoryEntityMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.netty.handler.timeout.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class ProductAdapter implements ProductRepository {

  private final WebClient webClient;

  private final RepositoryEntityMapper mapper;

  public ProductAdapter(WebClient webClient, RepositoryEntityMapper mapper) {
    this.webClient = webClient;
    this.mapper = mapper;
  }

  @CircuitBreaker(name = "similarProducts")
  @Retry(name = "similarProductsRetry")
  @Override
  public List<String> findSimilarProductsById(String productId) {
    try {
      return this.webClient.get()
          .uri("/product/{id}/similarids", productId)
          .retrieve()
          .bodyToMono(SimilarProductsEntity.class)
          .blockOptional()
          .map(this.mapper::toDomain)
          .orElse(List.of());
    } catch (final TimeoutException ex) {
      log.error("Timeout error fetching similar products for productId {}.", productId, ex);
      throw new ProductDetailTimeoutException("Timeout error fetching similar products.");
    } catch (final WebClientRequestException ex) {
      log.error("Connection error fetching similar products for productId {}.", productId, ex);
      throw new ProductConnectionException("Error fetching similar products.");
    }
  }

  @CircuitBreaker(name = "productDetails")
  @Retry(name = "productDetailsRetry")
  @Override
  public ProductDetails findProductDetailsById(String productId) {
    try {
      return this.webClient.get()
          .uri("/product/{id}", productId)
          .retrieve()
          .onStatus(status -> status.value() == 404,
              clientResponse -> Mono.error(new ProductDetailNotFoundException()))
          .onStatus(HttpStatusCode::is5xxServerError,
              clientResponse -> Mono.error(new ProductDetailErrorException()))
          .bodyToMono(ProductDetailsEntity.class)
          .map(this.mapper::toDomain)
          .block();
    } catch (final TimeoutException ex) {
      log.error("Timeout error fetching product details for productId {}.", productId, ex);
      throw new ProductDetailTimeoutException("Timeout error fetching product details.");
    } catch (final WebClientRequestException ex) {
      log.error("Connection error fetching product details for productId {}.", productId, ex);
      throw new ProductConnectionException("Error fetching product details.");
    }
  }
}
