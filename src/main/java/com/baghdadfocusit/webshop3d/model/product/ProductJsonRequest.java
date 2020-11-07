package com.baghdadfocusit.webshop3d.model.product;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductJsonRequest {

    @NotNull
    private String productName;
    @NotNull
    private double productPrice;
    @NotNull
    private String categoryId;
    @NotNull
    private MultipartFile productImage;
}
