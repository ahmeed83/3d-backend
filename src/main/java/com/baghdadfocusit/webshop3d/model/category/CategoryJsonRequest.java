package com.baghdadfocusit.webshop3d.model.category;

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
public class CategoryJsonRequest {

    private String id;
    @NotNull
    private String name;
    @NotNull
    private MultipartFile img;
}
