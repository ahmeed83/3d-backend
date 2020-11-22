package com.baghdadfocusit.webshop3d.model.product;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
    private String description;
    private boolean recommended;
    private boolean sale;
    private boolean outOfStock;
    @NotNull
    private MultipartFile productImage;
}