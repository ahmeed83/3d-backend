package com.baghdadfocusit.webshop3d.model.product;


import com.baghdadfocusit.webshop3d.model.category.CategoryJsonResponse;
import com.baghdadfocusit.webshop3d.model.common.ImageJsonResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductJsonResponse {

    @NotNull
    private UUID id;
    @NotNull
    private String name;
    @NotNull
    private double price;
    private double oldPrice;
    private double priceAssemble;
    private String description;
    private boolean sale;
    private boolean recommended;
    private boolean outOfStock;
    private boolean onlyShopAvailable;
    private List<ImageJsonResponse> imageJsonResponses;
    @NotNull
    private String picLocation;
    @NotNull
    private CategoryJsonResponse category;
}
