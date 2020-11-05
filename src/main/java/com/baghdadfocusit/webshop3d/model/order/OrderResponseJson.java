package com.baghdadfocusit.webshop3d.model.order;

import com.baghdadfocusit.webshop3d.entities.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseJson {

    @NotNull
    private UUID id;    
    @NotNull
    private LocalDate localDate;
    @NotNull
    private String city;
    @NotNull
    private String name;
    @NotNull
    private String orderTrackId;
    @NotNull
    private double totalAmount;
    @NotNull
    private Order.OrderState orderState;
    private String companyName;
    @NotNull
    private String district;
    private String district2;
    @NotNull
    private String mobileNumber;
    private String email;
    private String notes;
    private int itemAmount;
    @NotNull
    private List<OrderProductsResponse> orderProductsResponse;
}
