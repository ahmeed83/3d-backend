package com.baghdadfocusit.webshop3d.configuration.security.auth;

import com.baghdadfocusit.webshop3d.entities.BaseModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Application User class. This class will store the user data in the database.
 */
@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table(name = "APPLICATION_USER")
public class ApplicationUser extends BaseModel {

    @NotBlank(message = "Username is required")
    @Column(unique = true)
    private String userName;
    @NotBlank(message = "Password is required")
    private String password;
    @NotBlank(message = "Password does not match")
    @Transient
    private String passwordConfirm;
    @JsonInclude(Include.NON_NULL)
    @NotBlank
    private String role;
    @JsonInclude(Include.NON_NULL)
    @NotNull
    private boolean isEnabled;
}