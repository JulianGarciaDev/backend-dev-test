package dev.juliangarcia.similarproducts.domain.exception;

import java.io.Serial;

public class ProductDetailTimeoutException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = -2608871093465252086L;

  public ProductDetailTimeoutException(final String message) {
    super(message);
  }
}
