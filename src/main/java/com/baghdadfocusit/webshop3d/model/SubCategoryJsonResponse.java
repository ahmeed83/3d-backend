package com.baghdadfocusit.webshop3d.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubCategoryJsonResponse {

    private UUID id;
    @NotNull
    private String name;
    @NotNull
    private String categoryId;
}
