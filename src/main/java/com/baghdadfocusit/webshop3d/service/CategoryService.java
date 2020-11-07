package com.baghdadfocusit.webshop3d.service;

import com.baghdadfocusit.webshop3d.entities.Category;
import com.baghdadfocusit.webshop3d.exception.category.CategoryAlreadyExistsException;
import com.baghdadfocusit.webshop3d.model.CategoryJsonResponse;
import com.baghdadfocusit.webshop3d.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;

    /**
     * Constructor.
     *
     * @param categoryRepository    categoryRepository
     */
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Get all categories.
     *
     * @return categories
     */
    public List<CategoryJsonResponse> getAllCategories() {
        List<Category> categories = (List<Category>) categoryRepository.findAll();
        return categories.stream()
                .map(category -> new CategoryJsonResponse(category.getId().toString(),
                                                          category.getName(),
                                                          category.getImg()))
                .collect(Collectors.toList());
    }

    /**
     * Create category.
     *
     * @param categoryJson category json
     * @return category name created
     */
    public String creatCategoryAndGetCategoryName(final CategoryJsonResponse categoryJson) {
        categoryRepository.findCategoryByNameIgnoreCase(categoryJson.getName()).
                ifPresent(s -> {
                    throw new CategoryAlreadyExistsException();
                });
        final Category category = Category.builder()
                .name(categoryJson.getName())
                .createdAt(LocalDate.now())
                .build();
        final var savedCategory = categoryRepository.save(category);
        LOGGER.info("Category is saved with category Id: {}", savedCategory.getId());
        return savedCategory.getName();
    }
}
