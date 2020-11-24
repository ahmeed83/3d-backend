package com.baghdadfocusit.webshop3d.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@SuperBuilder
@Getter
@NoArgsConstructor
public class Image extends BaseModel {
    
    @NotNull
    private String picLocation;

    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    @ManyToOne(targetEntity = Product.class, fetch = FetchType.EAGER)
    private Product product;

    @NotNull
    @Column(name = "product_id")
    private UUID productId;
}
