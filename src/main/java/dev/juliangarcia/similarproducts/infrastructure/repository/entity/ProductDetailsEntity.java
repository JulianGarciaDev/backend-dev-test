package dev.juliangarcia.similarproducts.infrastructure.repository.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDetailsEntity {
  @JsonProperty("id")
  private String id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("price")
  private double price;

  @JsonProperty("availability")
  private boolean availability;
}
