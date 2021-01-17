package com.baghdadfocusit.webshop3d.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class Product extends BaseModel {

    @NotNull
    private String name;
    @NotNull
    private String picLocation;
    private String homeScreenPicLocation;
    @NotNull
    private double price;
    private double oldPrice;
    private double priceAssemble;
    private String description;
    private boolean outOfStock;
    private boolean onlyShopAvailable;
    private boolean sale;
    private boolean recommended;
    private boolean comingSoon;
    
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    @ManyToOne(targetEntity = Category.class, fetch = FetchType.EAGER)
    private Category category;

    @NotNull
    @Column(name = "category_id")
    private UUID categoryId;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orders = new HashSet<>();

    @OneToMany(mappedBy="product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();
}
