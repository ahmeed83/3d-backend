package com.baghdadfocusit.webshop3d.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryJsonResponse {

    @NotNull
    private String id;
    @NotNull
    private String name;
    @NotNull
    private String img;
}
