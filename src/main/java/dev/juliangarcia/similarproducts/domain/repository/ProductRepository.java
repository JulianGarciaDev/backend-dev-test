package dev.juliangarcia.similarproducts.domain.repository;

import java.util.List;

import dev.juliangarcia.similarproducts.domain.entity.ProductDetails;

public interface ProductRepository {

  List<String> findSimilarProductsById(String productId);

  ProductDetails findProductDetailsById(String productId);
}
