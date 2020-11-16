package com.baghdadfocusit.webshop3d.service;

import com.baghdadfocusit.webshop3d.entities.Order;
import com.baghdadfocusit.webshop3d.entities.Product;
import com.baghdadfocusit.webshop3d.exception.order.OrderNotFoundException;
import com.baghdadfocusit.webshop3d.exception.product.ProductNotFoundException;
import com.baghdadfocusit.webshop3d.model.order.OrderProductRequestJson;
import com.baghdadfocusit.webshop3d.model.order.OrderProductsResponse;
import com.baghdadfocusit.webshop3d.model.order.OrderRequestJson;
import com.baghdadfocusit.webshop3d.model.order.OrderResponseJson;
import com.baghdadfocusit.webshop3d.model.order.OrderStatusResponse;
import com.baghdadfocusit.webshop3d.model.order.OrderStatusUpdateRequest;
import com.baghdadfocusit.webshop3d.repository.OrderRepository;
import com.baghdadfocusit.webshop3d.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService;

    public OrderService(final OrderRepository orderRepository, final ProductRepository productRepository,
                        final EmailService emailService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.emailService = emailService;
    }

    public Page<OrderResponseJson> getFilterOrders(Optional<Integer> page, Optional<String> sortBy) {
        Page<Order> orderPage;
        if (sortBy.isPresent()) {
            orderPage = orderRepository.findAll(
                    PageRequest.of(page.orElse(0), 25, Sort.Direction.ASC, sortBy.orElse("name")));
        } else {
            orderPage = orderRepository.findAll(PageRequest.of(page.orElse(0), 25, Sort.unsorted()));
        }
        return new PageImpl<>(orderPage.getContent().stream().map(order -> {
            var orderProductsResponse = order.getOrderItems()
                    .stream()
                    .map(orderItem -> new OrderProductsResponse(orderItem.getProduct().getName(),
                                                                orderItem.getProduct().getPrice(), orderItem.getCount(),
                                                                orderItem.getAmount()))
                    .collect(Collectors.toList());
            return new OrderResponseJson(order.getId(), order.getCreatedAt(), order.getCity(), order.getName(),
                                         order.getOrderTrackId(), order.getTotalAmount(), order.getOrderState(),
                                         order.getCompanyName(), order.getDistrict(), order.getDistrict2(),
                                         order.getMobileNumber(), order.getEmail(), order.getNotes(),
                                         orderProductsResponse.size(), orderProductsResponse);
        }).collect(Collectors.toList()), orderPage.getPageable(), orderPage.getTotalElements());
    }


    /**
     * Create order by the customer.
     *
     * @param orderJson orderJson
     * @return order id. The customer can track his order by this ID
     */
    public OrderResponseJson creatOrder(final OrderRequestJson orderJson) {

        Order order = new Order();
        final Set<Product> products = new HashSet<>();
        for (OrderProductRequestJson orderedProduct : orderJson.getOrderedProducts()) {
            Product product = productRepository.findById(UUID.fromString(orderedProduct.getProductId()))
                    .orElseThrow(ProductNotFoundException::new);
            order.addProduct(product, orderedProduct.getProductCount(),
                             orderedProduct.getProductCount() * product.getPrice());
            products.add(product);
        }

        final double totalAmount = products.stream().mapToDouble(Product::getPrice).sum();
        final Random orderTrackId = new Random();
        final String format = String.format("3D-" + "%04d", orderTrackId.nextInt(10000));

        order.setCreatedAt(LocalDate.now());
        order.setOrderState(Order.OrderState.RECEIVED);
        order.setTotalAmount(totalAmount);
        order.setOrderTrackId(format);
        order.setName(orderJson.getName());
        order.setCity(orderJson.getCity());
        order.setDistrict(orderJson.getDistrict());
        order.setDistrict2(orderJson.getDistrict2());
        order.setEmail(orderJson.getEmail());
        order.setMobileNumber(orderJson.getMobileNumber());
        order.setNotes(orderJson.getNotes());
        order.setCompanyName(orderJson.getCompanyName());

        final var savedOrder = orderRepository.save(order);
        try {
            emailService.sendEmailToAdminWithOrder(order, products);
        } catch (MessagingException e) {
            LOGGER.info("Email failed to be sent", e);
        }

        LOGGER.info("Order is successfully saved with category Id: {}", savedOrder.getId());
        return new OrderResponseJson(order.getId(), order.getCreatedAt(), order.getCity(), order.getName(),
                                     order.getOrderTrackId(), order.getTotalAmount(), order.getOrderState(),
                                     order.getCompanyName(), order.getDistrict(), order.getDistrict2(),
                                     order.getMobileNumber(), order.getEmail(), order.getNotes(),
                                     order.getProducts().size(), order.getOrderItems()
                                             .stream()
                                             .map(orderItem -> new OrderProductsResponse(
                                                     orderItem.getProduct().getName(),
                                                     orderItem.getProduct().getPrice(), orderItem.getCount(),
                                                     orderItem.getAmount()))
                                             .collect(Collectors.toList()));
    }

    public OrderStatusResponse checkStatusOrder(final String orderTrackId) {
        Order order = orderRepository.findOrderByOrderTrackId(orderTrackId).orElseThrow(OrderNotFoundException::new);
        LOGGER.info("Order with {} ID is successfully found", orderTrackId);
        return new OrderStatusResponse(order.getCreatedAt(), order.getName(), order.getOrderState());
    }

    public void updateOrderStatus(final OrderStatusUpdateRequest orderStatusUpdateRequest) {
        Order order = orderRepository.findOrderById(UUID.fromString(orderStatusUpdateRequest.getId()))
                .orElseThrow(OrderNotFoundException::new);
        order.setOrderState(orderStatusUpdateRequest.getOrderState());
        orderRepository.save(order);
        LOGGER.info("Order with {} ID is updated", orderStatusUpdateRequest.getId());
    }
}
