package dev.juliangarcia.similarproducts.domain.exception;

import java.io.Serial;

public class ProductDetailErrorException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = -5394760198964088312L;

  public ProductDetailErrorException() {
    super("Unexpected Product Error");
  }
}
