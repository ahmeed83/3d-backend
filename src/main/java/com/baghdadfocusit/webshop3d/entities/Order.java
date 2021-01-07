package com.baghdadfocusit.webshop3d.entities;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ORDER3D")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@NaturalIdCache
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@TypeDef(
        name = "pgsql_enum",
        typeClass = PostgreSQLEnumType.class
)
public class Order extends BaseModel {

    public enum OrderState {RECEIVED, IN_PROGRESS, SHIPPED, DELIVERED, COMPLETED, CANCELLED}

    @NotNull
    private double totalAmount;
    @NotNull
    @NaturalId
    private String orderTrackId;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "order_state")
    @Type( type = "pgsql_enum" )
    private OrderState orderState;
    @NotNull
    private String city;
    @NotNull
    private String name;
    private String companyName;
    @NotNull
    private String district;
    private String district2;
    @NotNull
    private String mobileNumber;
    private String email;
    private String notes;
    private String extraInfoOrder;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> products = new HashSet<>();

    public void addProduct(final Product product, final int count, final double amount) {
        final OrderItem orderItem = new OrderItem(this, product, count, amount);
        products.add(orderItem);
        product.getOrders().add(orderItem);
    }

    public Set<OrderItem> getOrderItems(Order this) {
        return this.getProducts();
    }
}

