package com.baghdadfocusit.webshop3d.repository;

import com.baghdadfocusit.webshop3d.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends CrudRepository<Order, UUID> {

    Page<Order> findOrdersByMobileNumberContainingIgnoreCase(@Param("mobileNumber") Optional<String> mobileNumber,
                                                             Pageable pageable);

    Optional<Order> findOrderByOrderTrackId(String orderTrackId);
    
    Page<Order> findAll(Pageable pageable);
    
    Optional<List<Order>> findOrdersByMobileNumber(String mobileNumber);
    
    Optional<Order> findOrderById(UUID orderId);
}
