package com.baghdadfocusit.webshop3d.model.order;

import com.baghdadfocusit.webshop3d.entities.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusResponse {

    @NotNull
    private LocalDateTime createdAt;
    @NotNull
    private String name;
    @NotNull
    private Order.OrderState orderState;
}
