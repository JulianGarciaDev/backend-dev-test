package dev.juliangarcia.similarproducts.infrastructure.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.juliangarcia.similarproducts.application.usecase.GetSimilarProductsUseCase;
import dev.juliangarcia.similarproducts.domain.entity.ProductDetails;
import dev.juliangarcia.similarproducts.domain.exception.ProductDetailErrorException;
import dev.juliangarcia.similarproducts.domain.exception.ProductDetailNotFoundException;
import dev.juliangarcia.similarproducts.infrastructure.controller.dto.ProductDetailDto;
import dev.juliangarcia.similarproducts.infrastructure.controller.mapper.ProductDetailDtoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SimilarProductsRestController.class)
class SimilarProductsRestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private GetSimilarProductsUseCase getSimilarProductsUseCase;

  @MockitoBean
  private ProductDetailDtoMapper mapper;

  @Test
  void shouldReturnSimilarProductsSuccessfully() throws Exception {
    final String productId = "1";
    final List<ProductDetails> productDetailsList = Arrays.asList(
        this.createProductDetails("2", "Product 2", 10.0, true),
        this.createProductDetails("3", "Product 3", 20.0, false));
    final List<ProductDetailDto> expectedDtoList = Arrays.asList(
        this.createProductDetailDto("2", "Product 2", 10.0, true),
        this.createProductDetailDto("3", "Product 3", 20.0, false));

    when(this.getSimilarProductsUseCase.execute(productId)).thenReturn(productDetailsList);
    when(this.mapper.toDto(productDetailsList)).thenReturn(expectedDtoList);

    this.mockMvc.perform(get("/product/{productId}/similar", productId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value("2"))
        .andExpect(jsonPath("$[0].name").value("Product 2"))
        .andExpect(jsonPath("$[0].price").value(10.0))
        .andExpect(jsonPath("$[0].availability").value(true))
        .andExpect(jsonPath("$[1].id").value("3"))
        .andExpect(jsonPath("$[1].name").value("Product 3"))
        .andExpect(jsonPath("$[1].price").value(20.0))
        .andExpect(jsonPath("$[1].availability").value(false));

    verify(this.getSimilarProductsUseCase).execute(productId);
    verify(this.mapper).toDto(productDetailsList);
  }

  @Test
  void shouldReturnEmptyListWhenNoSimilarProducts() throws Exception {
    final String productId = "1";
    final List<ProductDetails> emptyList = List.of();
    final List<ProductDetailDto> emptyDtoList = List.of();

    when(this.getSimilarProductsUseCase.execute(productId)).thenReturn(emptyList);
    when(this.mapper.toDto(emptyList)).thenReturn(emptyDtoList);

    this.mockMvc.perform(get("/product/{productId}/similar", productId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());

    verify(this.getSimilarProductsUseCase).execute(productId);
    verify(this.mapper).toDto(emptyList);
  }

  @Test
  void shouldReturn404WhenProductNotFound() throws Exception {
    final String productId = "999";

    when(this.getSimilarProductsUseCase.execute(productId))
        .thenThrow(new ProductDetailNotFoundException());

    this.mockMvc.perform(get("/product/{productId}/similar", productId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$").value("Product Not found"));

    verify(this.getSimilarProductsUseCase).execute(productId);
  }

  @Test
  void shouldReturn500WhenInternalError() throws Exception {
    final String productId = "1";

    when(this.getSimilarProductsUseCase.execute(productId))
        .thenThrow(new ProductDetailErrorException());

    this.mockMvc.perform(get("/product/{productId}/similar", productId))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$").value("Unexpected Product Error"));

    verify(this.getSimilarProductsUseCase).execute(productId);
  }

  @Test
  void shouldReturn500WhenUnexpectedError() throws Exception {
    final String productId = "1";

    when(this.getSimilarProductsUseCase.execute(productId))
        .thenThrow(new RuntimeException("Unexpected error"));

    this.mockMvc.perform(get("/product/{productId}/similar", productId))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$").value("An unexpected error occurred."));

    verify(this.getSimilarProductsUseCase).execute(productId);
  }

  private ProductDetails createProductDetails(String id, String name, double price, boolean availability) {
    final ProductDetails productDetails = new ProductDetails();
    productDetails.setProductId(id);
    productDetails.setName(name);
    productDetails.setPrice(price);
    productDetails.setAvailability(availability);
    return productDetails;
  }

  private ProductDetailDto createProductDetailDto(String id, String name, double price, boolean availability) {
    final ProductDetailDto dto = new ProductDetailDto();
    dto.setId(id);
    dto.setName(name);
    dto.setPrice(price);
    dto.setAvailability(availability);
    return dto;
  }
}
