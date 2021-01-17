package com.baghdadfocusit.webshop3d.model.product;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductHomePageImageJsonResponse {

    @NotNull
    private UUID productId;
    
    private String productHomeScreenPicLocation;
}