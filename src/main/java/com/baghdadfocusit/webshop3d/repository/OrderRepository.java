package com.baghdadfocusit.webshop3d.repository;

import com.baghdadfocusit.webshop3d.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends CrudRepository<Order, UUID> {

    @Query("SELECT o FROM Order o WHERE UPPER(o.name)" + " LIKE CONCAT" + "('%',UPPER(:orderName),'%')")
    Page<Order> getFilterOrders(@Param("orderName") String orderName, Pageable pageable);

    Optional<Order> findOrderByOrderTrackId(String trackId);
    
    Optional<Order> findOrderById(UUID orderId);
}
