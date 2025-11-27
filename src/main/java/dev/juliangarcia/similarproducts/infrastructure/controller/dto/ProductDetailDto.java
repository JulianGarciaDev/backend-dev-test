package dev.juliangarcia.similarproducts.infrastructure.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDetailDto {
  private String id;

  private String name;

  private double price;

  private boolean availability;
}
