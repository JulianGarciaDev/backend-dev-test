package dev.juliangarcia.similarproducts.domain.exception;

import java.io.Serial;

public class ProductDetailNotFoundException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 4019658371365025084L;

	public ProductDetailNotFoundException() {
		super("Product Not found");
	}
}
