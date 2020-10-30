package com.baghdadfocusit.webshop3d.controller;

import com.baghdadfocusit.webshop3d.model.product.ProductJsonResponse;
import com.baghdadfocusit.webshop3d.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
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

    @GetMapping("all")
    public ResponseEntity<List<ProductJsonResponse>> getAllProducts(@RequestParam Optional<String> name,
                                                                    @RequestParam Optional<String> categoryName,
                                                                    @RequestParam Optional<Integer> page,
                                                                    @RequestParam Optional<String> sortBy) {
        var products = productService.getFilterProducts(name, categoryName, page, sortBy);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Range", String.valueOf(products.getTotalElements()));
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(products.getContent());
    }

    @GetMapping
    public ResponseEntity<Page<ProductJsonResponse>> getProductsByCategory(@RequestParam Optional<String> categoryId,
                                                               @RequestParam Optional<Integer> page,
                                                               @RequestParam Optional<String> sortBy) {
        return ResponseEntity.ok(productService.getProductsByCategoryId(categoryId, page, sortBy));
    }

    @GetMapping("recommended")
    public ResponseEntity<List<ProductJsonResponse>> getRecommendedProducts() {
        var recommendedProducts = productService.getRecommendedProducts();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Range", String.valueOf(recommendedProducts.size()));
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(recommendedProducts);
    }
}
