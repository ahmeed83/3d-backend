package com.baghdadfocusit.webshop3d.model.order;

import com.baghdadfocusit.webshop3d.entities.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusUpdateRequest {
    
    @NotNull
    private String id;
    @NotNull
    private Order.OrderState orderState;
}
