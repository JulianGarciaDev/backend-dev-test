package dev.juliangarcia.similarproducts.domain.exception;

import java.io.Serial;

public class ProductConnectionException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 3567934136950346800L;

  public ProductConnectionException(String message) {
    super(message);
  }
}
