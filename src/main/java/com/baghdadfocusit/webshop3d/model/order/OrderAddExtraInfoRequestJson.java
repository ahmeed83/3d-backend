package com.baghdadfocusit.webshop3d.model.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderAddExtraInfoRequestJson {

    @NotNull
    private String id;
    @NotNull
    private String extraInfoOrder;
}
