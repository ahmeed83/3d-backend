package com.baghdadfocusit.webshop3d.controller;

import com.baghdadfocusit.webshop3d.model.category.CategoryJsonResponse;
import com.baghdadfocusit.webshop3d.service.CategoryService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Product controller.
 */
@RestController
@RequestMapping("resources/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    @GetMapping()
    public ResponseEntity<List<CategoryJsonResponse>> getCategories() {
        var allCategories = categoryService.getAllCategories();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Range", String.valueOf(allCategories.size()));
        return ResponseEntity.accepted()
                .headers(responseHeaders)
                .body(allCategories);
    }
}
