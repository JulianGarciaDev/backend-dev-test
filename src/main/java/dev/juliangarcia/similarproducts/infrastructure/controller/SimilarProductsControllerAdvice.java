package dev.juliangarcia.similarproducts.infrastructure.controller;

import java.util.concurrent.CompletionException;

import dev.juliangarcia.similarproducts.domain.exception.ProductConnectionException;
import dev.juliangarcia.similarproducts.domain.exception.ProductDetailErrorException;
import dev.juliangarcia.similarproducts.domain.exception.ProductDetailNotFoundException;
import dev.juliangarcia.similarproducts.domain.exception.ProductDetailTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class SimilarProductsControllerAdvice {

  @ExceptionHandler(ProductDetailNotFoundException.class)
  public ResponseEntity<String> handleProductDetailNotFoundException(ProductDetailNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(ProductDetailTimeoutException.class)
  public ResponseEntity<String> handleProductDetailTimeoutException(ProductDetailTimeoutException ex) {
    return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(ex.getMessage());
  }

  @ExceptionHandler(ProductDetailErrorException.class)
  public ResponseEntity<String> handleProductDetailErrorException(ProductDetailErrorException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
  }

  @ExceptionHandler(ProductConnectionException.class)
  public ResponseEntity<String> handleProductConnectionException(ProductConnectionException ex) {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ex.getMessage());
  }

  @ExceptionHandler(CompletionException.class)
  public ResponseEntity<String> handleCompletionException(CompletionException ex) {
    final Throwable cause = ex.getCause();

    if (cause instanceof ProductDetailNotFoundException productDetailNotFoundException) {
      return this.handleProductDetailNotFoundException(productDetailNotFoundException);
    }

    if (cause instanceof ProductDetailErrorException productDetailErrorException) {
      return this.handleProductDetailErrorException(productDetailErrorException);
    }

    if (cause instanceof ProductDetailTimeoutException productDetailTimeoutException) {
      return this.handleProductDetailTimeoutException(productDetailTimeoutException);
    }

    if (cause instanceof ProductConnectionException productConnectionException) {
      return this.handleProductConnectionException(productConnectionException);
    }

    return this.handleGenericException(ex);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handleGenericException(RuntimeException ex) {
    log.error("An unexpected error occurred.", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
  }
}
