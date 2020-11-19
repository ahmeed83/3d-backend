package com.baghdadfocusit.webshop3d.model.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestJson {

    private String orderTrackId;
    @NotNull
    private String name;
    private String companyName;
    @NotNull
    private String city;
    @NotNull
    private String district;
    private String district2;
    @NotNull
    private String mobileNumber;
    private double totalAmount;
    private String email;
    private String notes;
    @NotNull
    private Set<OrderProductRequestJson> orderedProducts = new HashSet<>();
}
