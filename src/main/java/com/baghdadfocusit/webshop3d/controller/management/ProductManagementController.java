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
import org.springframework.web.bind.annotation.PutMapping;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public ResponseEntity<HttpStatus> createProduct(@ModelAttribute @Valid ProductJsonRequest product) {
        productService.createProduct(product);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    @PutMapping()
    public ResponseEntity<HttpStatus> editProduct(@ModelAttribute @Valid ProductJsonRequest product) {
        productService.editProduct(product);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("make-recommended/{productId}")
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public ResponseEntity<HttpStatus> makeProductRecommended(@PathVariable String productId) {
        productService.makeRecommended(productId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("make-out-of-stock/{productId}")
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public ResponseEntity<HttpStatus> makeOutOfStockRecommended(@PathVariable String productId) {
        productService.makeOutOfStockRecommended(productId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("{productId}")
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable String productId) {
        productService.deleteProduct(productId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PatchMapping("{productId}")
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public ResponseEntity<HttpStatus> updateProductPrice(@PathVariable String productId,
                                                         @RequestBody ProductUpdatePriceRequest updatePriceRequest) {
        productService.updateProductPrice(productId, updatePriceRequest);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("recommended")
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public ResponseEntity<Page<ProductJsonResponse>> getFilteredRecommendedProducts(
            @RequestParam Optional<Integer> page, @RequestParam Optional<String> sortBy) {
        return new ResponseEntity<>(productService.getFilteredRecommendedProducts(page, sortBy), HttpStatus.OK);
    }

    @GetMapping("search")
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public ResponseEntity<Page<ProductJsonResponse>> searchProductByName(@RequestParam Optional<String> productName,
                                                                         @RequestParam Optional<Integer> page,
                                                                         @RequestParam Optional<String> sortBy) {
        if (productName.isPresent() && productName.get().length() < 5) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(productService.searchProductByName(productName, page, sortBy), HttpStatus.OK);
    }

}
