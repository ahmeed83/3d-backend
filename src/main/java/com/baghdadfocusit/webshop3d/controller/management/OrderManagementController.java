package com.baghdadfocusit.webshop3d.controller.management;

import com.baghdadfocusit.webshop3d.model.order.OrderRequestJson;
import com.baghdadfocusit.webshop3d.model.order.OrderResponseJson;
import com.baghdadfocusit.webshop3d.model.order.OrderStatusUpdateRequest;
import com.baghdadfocusit.webshop3d.model.product.ProductJsonRequest;
import com.baghdadfocusit.webshop3d.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public ResponseEntity<Page<OrderResponseJson>> getAllFilterOrders(@RequestParam Optional<Integer> page,
                                                                      @RequestParam Optional<String> sortBy) {
        return new ResponseEntity<>(orderService.getFilterOrders(page, sortBy), HttpStatus.OK);

    }

    @GetMapping("search")
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public ResponseEntity<Page<OrderResponseJson>> getFilterOrdersByMobileNumber(
            @RequestParam Optional<String> mobileNumber, @RequestParam Optional<Integer> page,
            @RequestParam Optional<String> sortBy) {
        if (mobileNumber.isPresent() && mobileNumber.get().length() < 7) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(orderService.getFilterOrdersByMobileNumber(mobileNumber, page, sortBy),
                                    HttpStatus.OK);

    }

    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    @PutMapping()
    public ResponseEntity<HttpStatus> editProduct(@ModelAttribute @Valid OrderRequestJson orderRequest) {
        orderService.editOrder(orderRequest);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public ResponseEntity<HttpStatus> updateOrderStatus(
            @RequestBody @Valid OrderStatusUpdateRequest orderStatusUpdateRequest) {
        orderService.updateOrderStatus(orderStatusUpdateRequest);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
