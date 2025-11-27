package dev.juliangarcia.similarproducts.infrastructure.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CompletionException;

import dev.juliangarcia.similarproducts.domain.exception.ProductConnectionException;
import dev.juliangarcia.similarproducts.domain.exception.ProductDetailErrorException;
import dev.juliangarcia.similarproducts.domain.exception.ProductDetailNotFoundException;
import dev.juliangarcia.similarproducts.domain.exception.ProductDetailTimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class SimilarProductsControllerAdviceTest {

  private SimilarProductsControllerAdvice controllerAdvice;

  @BeforeEach
  void setUp() {
    this.controllerAdvice = new SimilarProductsControllerAdvice();
  }

  @Test
  void shouldHandleProductDetailNotFoundException() {
    final ProductDetailNotFoundException exception = new ProductDetailNotFoundException();

    final ResponseEntity<String> response = this.controllerAdvice.handleProductDetailNotFoundException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isEqualTo("Product Not found");
  }

  @Test
  void shouldHandleProductDetailTimeoutException() {
    final ProductDetailTimeoutException exception = new ProductDetailTimeoutException("Timeout error");

    final ResponseEntity<String> response = this.controllerAdvice.handleProductDetailTimeoutException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
    assertThat(response.getBody()).isEqualTo("Timeout error");
  }

  @Test
  void shouldHandleProductDetailErrorException() {
    final ProductDetailErrorException exception = new ProductDetailErrorException();

    final ResponseEntity<String> response = this.controllerAdvice.handleProductDetailErrorException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isEqualTo("Unexpected Product Error");
  }

  @Test
  void shouldHandleProductConnectionException() {
    final ProductConnectionException exception = new ProductConnectionException("Connection error");

    final ResponseEntity<String> response = this.controllerAdvice.handleProductConnectionException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    assertThat(response.getBody()).isEqualTo("Connection error");
  }

  @Test
  void shouldHandleCompletionExceptionWithProductDetailNotFoundException() {
    final ProductDetailNotFoundException cause = new ProductDetailNotFoundException();
    final CompletionException exception = new CompletionException(cause);

    final ResponseEntity<String> response = this.controllerAdvice.handleCompletionException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isEqualTo("Product Not found");
  }

  @Test
  void shouldHandleCompletionExceptionWithProductDetailErrorException() {
    final ProductDetailErrorException cause = new ProductDetailErrorException();
    final CompletionException exception = new CompletionException(cause);

    final ResponseEntity<String> response = this.controllerAdvice.handleCompletionException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isEqualTo("Unexpected Product Error");
  }

  @Test
  void shouldHandleCompletionExceptionWithProductDetailTimeoutException() {
    final ProductDetailTimeoutException cause = new ProductDetailTimeoutException("Timeout error");
    final CompletionException exception = new CompletionException(cause);

    final ResponseEntity<String> response = this.controllerAdvice.handleCompletionException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
    assertThat(response.getBody()).isEqualTo("Timeout error");
  }

  @Test
  void shouldHandleCompletionExceptionWithProductConnectionException() {
    final ProductConnectionException cause = new ProductConnectionException("Connection error");
    final CompletionException exception = new CompletionException(cause);

    final ResponseEntity<String> response = this.controllerAdvice.handleCompletionException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    assertThat(response.getBody()).isEqualTo("Connection error");
  }

  @Test
  void shouldHandleCompletionExceptionWithUnknownCause() {
    final RuntimeException cause = new RuntimeException("Unknown error");
    final CompletionException exception = new CompletionException(cause);

    final ResponseEntity<String> response = this.controllerAdvice.handleCompletionException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isEqualTo("An unexpected error occurred.");
  }

  @Test
  void shouldHandleGenericException() {
    final RuntimeException exception = new RuntimeException("Generic error");

    final ResponseEntity<String> response = this.controllerAdvice.handleGenericException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isEqualTo("An unexpected error occurred.");
  }
}
