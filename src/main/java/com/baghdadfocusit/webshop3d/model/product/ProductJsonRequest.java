package com.baghdadfocusit.webshop3d.model.product;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductJsonRequest {

    private String id;
    @NotNull
    @Size(min = 5)
    private String productName;
    @NotNull
    private String categoryId;
    @NotNull
    private double productPrice;
    private double productOldPrice;
    private double priceAssemble;
    private String description;
    private boolean recommended;
    private boolean sale;
    private boolean outOfStock;
    private boolean onlyShopAvailable;
    private boolean comingSoon;
    private MultipartFile picLocation;
    private MultipartFile homeScreenPicLocation;
    private List<MultipartFile> productImages;
}