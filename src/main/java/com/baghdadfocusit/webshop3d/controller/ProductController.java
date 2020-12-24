package com.baghdadfocusit.webshop3d.controller;

import com.baghdadfocusit.webshop3d.model.product.ProductJsonResponse;
import com.baghdadfocusit.webshop3d.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Product controller.
 */
@RestController
@RequestMapping("resources/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("category-id")
    public ResponseEntity<Page<ProductJsonResponse>> getProductsByCategory(@RequestParam Optional<String> categoryId,
                                                                           @RequestParam Optional<Integer> page,
                                                                           @RequestParam Optional<String> sortBy) {
        return new ResponseEntity<>(productService.getProductsByCategoryId(categoryId, page, sortBy), HttpStatus.OK);
    }

    @GetMapping("product")
    public ResponseEntity<ProductJsonResponse> getOneProduct(@RequestParam String productId) {
        return new ResponseEntity<>(productService.getProductsById(productId), HttpStatus.OK);
    }

    @GetMapping("recommended")
    public ResponseEntity<List<ProductJsonResponse>> getRecommendedProducts() {
        return new ResponseEntity<>(productService.getRecommendedProducts(), HttpStatus.OK);
    }

    @GetMapping("search")
    public ResponseEntity<Page<ProductJsonResponse>> searchProductByName(@RequestParam Optional<String> productName,
                                                                         @RequestParam Optional<Integer> page,
                                                                         @RequestParam Optional<String> sortBy) {
        return new ResponseEntity<>(productService.searchProductByName(productName, page, sortBy), HttpStatus.OK);
    }
}
