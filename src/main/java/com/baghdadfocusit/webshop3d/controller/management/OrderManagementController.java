package com.baghdadfocusit.webshop3d.controller.management;

import com.baghdadfocusit.webshop3d.model.order.OrderResponseJson;
import com.baghdadfocusit.webshop3d.model.order.OrderStatusUpdateRequest;
import com.baghdadfocusit.webshop3d.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Order Management controller.
 */
@RestController
@RequestMapping("management/v1/orders")
public class OrderManagementController {

    private static final String HAS_ROLE_ADMIN_AND_EMPLOYEE = "hasAnyRole('ROLE_ADMIN, ROLE_EMPLOYEE')";

    private final OrderService orderService;

    public OrderManagementController(final OrderService orderService) {
        this.orderService = orderService;
    }


    @GetMapping
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public ResponseEntity<Page<OrderResponseJson>> getAllFilterOrders(@RequestParam Optional<String> name,
                                                                      @RequestParam Optional<Integer> page,
                                                                      @RequestParam Optional<String> sortBy) {
        return ResponseEntity.ok(orderService.getFilterOrders(name, page, sortBy));
    }

    @PostMapping
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public void updateOrderStatus(@RequestBody @Valid OrderStatusUpdateRequest orderStatusUpdateRequest) {
        orderService.updateOrderStatus(orderStatusUpdateRequest);
    }
}
