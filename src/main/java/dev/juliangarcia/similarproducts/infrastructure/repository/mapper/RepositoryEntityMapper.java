package dev.juliangarcia.similarproducts.infrastructure.repository.mapper;

import java.util.List;

import dev.juliangarcia.similarproducts.domain.entity.ProductDetails;
import dev.juliangarcia.similarproducts.infrastructure.repository.entity.ProductDetailsEntity;
import dev.juliangarcia.similarproducts.infrastructure.repository.entity.SimilarProductsEntity;
import org.springframework.stereotype.Component;

@Component
public class RepositoryEntityMapper {

  public ProductDetails toDomain(final ProductDetailsEntity entity) {
    final ProductDetails domain = new ProductDetails();
    if (entity == null) {
      return domain;
    }
    domain.setProductId(entity.getId());
    domain.setName(entity.getName());
    domain.setPrice(entity.getPrice());
    domain.setAvailability(entity.isAvailability());
    return domain;
  }

  public List<String> toDomain(final SimilarProductsEntity entity) {
    return entity.productIds();
  }

}
