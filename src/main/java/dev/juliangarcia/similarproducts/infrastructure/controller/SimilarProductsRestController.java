package dev.juliangarcia.similarproducts.infrastructure.controller;

import java.util.List;

import dev.juliangarcia.similarproducts.application.usecase.GetSimilarProductsUseCase;
import dev.juliangarcia.similarproducts.domain.entity.ProductDetails;
import dev.juliangarcia.similarproducts.infrastructure.controller.dto.ProductDetailDto;
import dev.juliangarcia.similarproducts.infrastructure.controller.mapper.ProductDetailDtoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class SimilarProductsRestController {

  private final GetSimilarProductsUseCase getSimilarProductsUseCase;

  private final ProductDetailDtoMapper mapper;

  public SimilarProductsRestController(GetSimilarProductsUseCase getSimilarProductsUseCase,
      ProductDetailDtoMapper mapper) {
    this.getSimilarProductsUseCase = getSimilarProductsUseCase;
    this.mapper = mapper;
  }

  @GetMapping("/{productId}/similar")
  public ResponseEntity<List<ProductDetailDto>> getSimilarProducts(@PathVariable final String productId) {
    final List<ProductDetails> productDetailsList = this.getSimilarProductsUseCase.execute(productId);
    final List<ProductDetailDto> result = this.mapper.toDto(productDetailsList);
    return ResponseEntity.ok(result);
  }
}
