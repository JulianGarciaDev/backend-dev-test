package dev.juliangarcia.similarproducts.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import dev.juliangarcia.similarproducts.domain.entity.ProductDetails;
import dev.juliangarcia.similarproducts.domain.exception.ProductConnectionException;
import dev.juliangarcia.similarproducts.domain.exception.ProductDetailErrorException;
import dev.juliangarcia.similarproducts.domain.exception.ProductDetailNotFoundException;
import dev.juliangarcia.similarproducts.domain.exception.ProductDetailTimeoutException;
import dev.juliangarcia.similarproducts.infrastructure.repository.entity.ProductDetailsEntity;
import dev.juliangarcia.similarproducts.infrastructure.repository.entity.SimilarProductsEntity;
import dev.juliangarcia.similarproducts.infrastructure.repository.mapper.RepositoryEntityMapper;
import io.netty.handler.timeout.ReadTimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class ProductAdapterTest {

  @Mock
  private WebClient webClient;

  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;

  @Mock
  private WebClient.ResponseSpec responseSpec;

  @Mock
  private RepositoryEntityMapper mapper;

  private ProductAdapter productAdapter;

  @BeforeEach
  void setUp() {
    this.productAdapter = new ProductAdapter(this.webClient, this.mapper);
  }

  @Test
  void shouldReturnSimilarProductIdsSuccessfully() {
    final String productId = "1";
    final List<String> expectedIds = List.of("2", "3", "4");
    final SimilarProductsEntity entity = new SimilarProductsEntity(expectedIds);

    when(this.webClient.get()).thenReturn(this.requestHeadersUriSpec);
    when(this.requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(this.requestHeadersSpec);
    when(this.requestHeadersSpec.retrieve()).thenReturn(this.responseSpec);
    when(this.responseSpec.bodyToMono(SimilarProductsEntity.class)).thenReturn(Mono.just(entity));
    when(this.mapper.toDomain(entity)).thenReturn(expectedIds);

    final List<String> result = this.productAdapter.findSimilarProductsById(productId);

    assertThat(result).hasSize(3).containsExactly("2", "3", "4");
  }

  @Test
  void shouldReturnEmptyListWhenNoSimilarProducts() {
    final String productId = "1";

    when(this.webClient.get()).thenReturn(this.requestHeadersUriSpec);
    when(this.requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(this.requestHeadersSpec);
    when(this.requestHeadersSpec.retrieve()).thenReturn(this.responseSpec);
    when(this.responseSpec.bodyToMono(SimilarProductsEntity.class)).thenReturn(Mono.empty());

    final List<String> result = this.productAdapter.findSimilarProductsById(productId);

    assertThat(result).isEmpty();
  }

  @Test
  void shouldThrowTimeoutExceptionWhenFetchingSimilarProducts() {
    final String productId = "1";

    when(this.webClient.get()).thenReturn(this.requestHeadersUriSpec);
    when(this.requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(this.requestHeadersSpec);
    when(this.requestHeadersSpec.retrieve()).thenReturn(this.responseSpec);
    when(this.responseSpec.bodyToMono(SimilarProductsEntity.class))
        .thenReturn(Mono.error(new ReadTimeoutException()));

    assertThatThrownBy(() -> this.productAdapter.findSimilarProductsById(productId))
        .isInstanceOf(ProductDetailTimeoutException.class)
        .hasMessageContaining("Timeout error fetching similar products.");
  }

  @Test
  void shouldThrowConnectionExceptionWhenFetchingSimilarProducts() {
    final String productId = "1";
    final WebClientRequestException requestException = mock(WebClientRequestException.class);

    when(this.webClient.get()).thenReturn(this.requestHeadersUriSpec);
    when(this.requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(this.requestHeadersSpec);
    when(this.requestHeadersSpec.retrieve()).thenReturn(this.responseSpec);
    when(this.responseSpec.bodyToMono(SimilarProductsEntity.class))
        .thenReturn(Mono.error(requestException));

    assertThatThrownBy(() -> this.productAdapter.findSimilarProductsById(productId))
        .isInstanceOf(ProductConnectionException.class)
        .hasMessageContaining("Error fetching similar products.");
  }

  @Test
  void shouldReturnProductDetailsSuccessfully() {
    final String productId = "1";
    final ProductDetailsEntity entity = this.createProductDetailsEntity("1", "Product 1", 10.0, true);
    final ProductDetails expected = this.createProductDetails("1", "Product 1", 10.0, true);

    when(this.webClient.get()).thenReturn(this.requestHeadersUriSpec);
    when(this.requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(this.requestHeadersSpec);
    when(this.requestHeadersSpec.retrieve()).thenReturn(this.responseSpec);
    when(this.responseSpec.onStatus(any(), any())).thenReturn(this.responseSpec);
    when(this.responseSpec.bodyToMono(ProductDetailsEntity.class)).thenReturn(Mono.just(entity));
    when(this.mapper.toDomain(entity)).thenReturn(expected);

    final ProductDetails result = this.productAdapter.findProductDetailsById(productId);

    assertThat(result).isNotNull();
    assertThat(result.getProductId()).isEqualTo("1");
    assertThat(result.getName()).isEqualTo("Product 1");
    assertThat(result.getPrice()).isEqualTo(10.0);
    assertThat(result.isAvailability()).isTrue();
  }

  @Test
  void shouldThrowNotFoundExceptionWhen404() {
    final String productId = "999";
    final ClientResponse clientResponse = ClientResponse
        .create(HttpStatus.NOT_FOUND)
        .build();

    when(this.webClient.get()).thenReturn(this.requestHeadersUriSpec);
    when(this.requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(this.requestHeadersSpec);
    when(this.requestHeadersSpec.retrieve()).thenReturn(this.responseSpec);
    when(this.responseSpec.onStatus(any(), any())).thenAnswer(invocationOnMock -> {
      final Predicate<HttpStatusCode> statusPredicate = invocationOnMock.getArgument(0);
      if (statusPredicate.test(HttpStatus.NOT_FOUND)) {
        final Function<ClientResponse, Mono<? extends Throwable>> errorHandler = invocationOnMock.getArgument(1);
        when(this.responseSpec.bodyToMono(ProductDetailsEntity.class)).thenReturn(errorHandler.apply(clientResponse).flatMap(Mono::error));
      }
      return this.responseSpec;
    });
    assertThatThrownBy(() -> this.productAdapter.findProductDetailsById(productId))
        .isInstanceOf(ProductDetailNotFoundException.class)
        .hasMessageContaining("Product Not found");
  }

  @Test
  void shouldThrowErrorExceptionWhen500() {
    final String productId = "1";
    final ClientResponse clientResponse = ClientResponse
        .create(HttpStatus.INTERNAL_SERVER_ERROR)
        .build();

    when(this.webClient.get()).thenReturn(this.requestHeadersUriSpec);
    when(this.requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(this.requestHeadersSpec);
    when(this.requestHeadersSpec.retrieve()).thenReturn(this.responseSpec);
    when(this.responseSpec.onStatus(any(), any())).thenAnswer(invocationOnMock -> {
      final Predicate<HttpStatusCode> statusPredicate = invocationOnMock.getArgument(0);
      if (statusPredicate.test(HttpStatus.INTERNAL_SERVER_ERROR)) {
        final Function<ClientResponse, Mono<? extends Throwable>> errorHandler = invocationOnMock.getArgument(1);
        when(this.responseSpec.bodyToMono(ProductDetailsEntity.class)).thenReturn(errorHandler.apply(clientResponse).flatMap(Mono::error));
      }
      return this.responseSpec;
    });

    assertThatThrownBy(() -> this.productAdapter.findProductDetailsById(productId))
        .isInstanceOf(ProductDetailErrorException.class)
        .hasMessageContaining("Unexpected Product Error");
  }

  @Test
  void shouldThrowTimeoutExceptionWhenFetchingProductDetails() {
    final String productId = "1";

    when(this.webClient.get()).thenReturn(this.requestHeadersUriSpec);
    when(this.requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(this.requestHeadersSpec);
    when(this.requestHeadersSpec.retrieve()).thenReturn(this.responseSpec);
    when(this.responseSpec.onStatus(any(), any())).thenReturn(this.responseSpec);
    when(this.responseSpec.bodyToMono(ProductDetailsEntity.class))
        .thenReturn(Mono.error(new ReadTimeoutException()));

    assertThatThrownBy(() -> this.productAdapter.findProductDetailsById(productId))
        .isInstanceOf(ProductDetailTimeoutException.class)
        .hasMessageContaining("Timeout error fetching product details.");
  }

  @Test
  void shouldThrowConnectionExceptionWhenFetchingProductDetails() {
    final String productId = "1";
    final WebClientRequestException requestException = mock(WebClientRequestException.class);

    when(this.webClient.get()).thenReturn(this.requestHeadersUriSpec);
    when(this.requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(this.requestHeadersSpec);
    when(this.requestHeadersSpec.retrieve()).thenReturn(this.responseSpec);
    when(this.responseSpec.onStatus(any(), any())).thenReturn(this.responseSpec);
    when(this.responseSpec.bodyToMono(ProductDetailsEntity.class))
        .thenReturn(Mono.error(requestException));

    assertThatThrownBy(() -> this.productAdapter.findProductDetailsById(productId))
        .isInstanceOf(ProductConnectionException.class)
        .hasMessageContaining("Error fetching product details.");
  }

  private ProductDetailsEntity createProductDetailsEntity(String id, String name, double price, boolean availability) {
    final ProductDetailsEntity entity = new ProductDetailsEntity();
    entity.setId(id);
    entity.setName(name);
    entity.setPrice(price);
    entity.setAvailability(availability);
    return entity;
  }

  private ProductDetails createProductDetails(String id, String name, double price, boolean availability) {
    final ProductDetails product = new ProductDetails();
    product.setProductId(id);
    product.setName(name);
    product.setPrice(price);
    product.setAvailability(availability);
    return product;
  }
}
