package dev.juliangarcia.similarproducts.domain.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDetails {
  private String productId;

  private String name;

  private double price;

  private boolean availability;

}
