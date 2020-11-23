package com.baghdadfocusit.webshop3d.model.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImageJsonResponse {

    @NotNull
    private String id;
    @NotBlank
    private String productImage;    
}
