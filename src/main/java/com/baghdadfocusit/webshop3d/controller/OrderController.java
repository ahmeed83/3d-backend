package com.baghdadfocusit.webshop3d.controller;

import com.baghdadfocusit.webshop3d.model.order.OrderRequestJson;
import com.baghdadfocusit.webshop3d.model.order.OrderResponseJson;
import com.baghdadfocusit.webshop3d.model.order.OrderStatusResponse;
import com.baghdadfocusit.webshop3d.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Order controller.
 */
@RestController
@RequestMapping("resources/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/add-order")
    public ResponseEntity<OrderResponseJson> createOrder(@RequestBody OrderRequestJson orderJson) {
        return new ResponseEntity<>(orderService.creatOrder(orderJson), HttpStatus.CREATED);
    }

    @GetMapping("/checkStatus/{orderTrackId}")
    public ResponseEntity<OrderStatusResponse> checkStatusOrder(@PathVariable final String orderTrackId) {
        
        return ResponseEntity.accepted()
                .body(orderService.checkStatusOrder(orderTrackId));
    }
}
