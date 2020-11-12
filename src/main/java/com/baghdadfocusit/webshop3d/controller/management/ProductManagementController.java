package com.baghdadfocusit.webshop3d.controller.management;

import com.baghdadfocusit.webshop3d.model.product.ProductJsonRequest;
import com.baghdadfocusit.webshop3d.model.product.ProductJsonResponse;
import com.baghdadfocusit.webshop3d.model.product.ProductUpdatePriceRequest;
import com.baghdadfocusit.webshop3d.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Product Management controller.
 */
@RestController
@RequestMapping("management/v1/products")
public class ProductManagementController {

    private static final String HAS_ROLE_ADMIN_AND_EMPLOYEE = "hasAnyRole('ROLE_ADMIN, ROLE_EMPLOYEE')";

    private final ProductService productService;

    public ProductManagementController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping()
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public ResponseEntity<Page<ProductJsonResponse>> getAllFilteredProducts(@RequestParam Optional<Integer> page,
                                                                            @RequestParam Optional<String> sortBy) {
        return new ResponseEntity<>(productService.getAllFilteredProducts(page, sortBy), HttpStatus.OK);
    }

    @GetMapping("make-recommended/{productId}")
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public ResponseEntity<Page<ProductJsonResponse>> makeProductRecommended(@PathVariable String productId) {
        productService.makeRecommended(productId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public ResponseEntity<String> createProduct(@ModelAttribute @Valid ProductJsonRequest product) {
        return new ResponseEntity<>(productService.createProductAndGetProductName(product),
                                    HttpStatus.CREATED);
    }

    @DeleteMapping("{productId}")
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable String productId) {
        //TODO: if product is not there return a proper exception with 404
        productService.deleteProduct(productId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PatchMapping("{productId}")
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public ResponseEntity<HttpStatus> updateProductPrice(@PathVariable String productId,
                                                         @RequestBody ProductUpdatePriceRequest updatePriceRequest) {
        //TODO: if product is not there return a proper exception with 404
        productService.updateProductPrice(productId, updatePriceRequest);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    //
    //  @PutMapping("{productId}")
    //  @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    //  public ResponseEntity<Product> updateProduct(@RequestBody @Valid Product updatedProduct,
    //                                               @PathVariable String productId) {
    //    return productService.findProduct(productId).map(product -> {
    //      product.setName(updatedProduct.getName());
    //      product.setPrice(updatedProduct.getPrice());
    //      //TODO: check how to get the pic from AMAZON
    //      product.setPicLocation(updatedProduct.getPicLocation());
    //      product.setUpdatedAt(LocalDate.now());
    //      return new ResponseEntity<>(productService.createNewProduct(product), HttpStatus.OK);
    //    }).orElseThrow(() -> new IllegalArgumentException("No Product found!"));
    //  }
}
