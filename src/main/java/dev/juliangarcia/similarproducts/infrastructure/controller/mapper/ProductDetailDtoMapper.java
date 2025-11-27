package dev.juliangarcia.similarproducts.infrastructure.controller.mapper;

import java.util.List;

import dev.juliangarcia.similarproducts.domain.entity.ProductDetails;
import dev.juliangarcia.similarproducts.infrastructure.controller.dto.ProductDetailDto;
import org.springframework.stereotype.Component;

@Component
public class ProductDetailDtoMapper {
  public ProductDetailDto toDto(ProductDetails productDetails) {
    final ProductDetailDto dto = new ProductDetailDto();
    if (productDetails == null) {
      return dto;
    }
    dto.setId(productDetails.getProductId());
    dto.setName(productDetails.getName());
    dto.setPrice(productDetails.getPrice());
    dto.setAvailability(productDetails.isAvailability());
    return dto;
  }

  public List<ProductDetailDto> toDto(List<ProductDetails> productDetailsList) {
    return productDetailsList.stream()
        .map(this::toDto)
        .toList();
  }
}
