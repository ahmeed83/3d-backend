package com.baghdadfocusit.webshop3d.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class Category extends BaseModel {

    @NotNull
    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "picLocation")
    private String img;
}
