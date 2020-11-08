package com.baghdadfocusit.webshop3d.service;

import com.baghdadfocusit.webshop3d.entities.Category;
import com.baghdadfocusit.webshop3d.exception.category.CategoryAlreadyExistsException;
import com.baghdadfocusit.webshop3d.model.category.CategoryJsonRequest;
import com.baghdadfocusit.webshop3d.model.category.CategoryJsonResponse;
import com.baghdadfocusit.webshop3d.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;

    /**
     * Constructor.
     *
     * @param categoryRepository categoryRepository
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
     * Get Filtered categories.
     *
     * @return categories
     */
    public Page<CategoryJsonResponse> getFilterCategories(Optional<Integer> page, Optional<String> sortBy) {
        Page<Category> categoriesPage;
        if (sortBy.isPresent()) {
            categoriesPage = categoryRepository.findAll(PageRequest.of(page.orElse(0), 15, Sort.Direction.ASC,
                                                                       sortBy.orElse("name")));
        } else {
            categoriesPage = categoryRepository.findAll(PageRequest.of(page.orElse(0), 15, Sort.unsorted()));
        }
        return new PageImpl<>(categoriesPage.getContent()
                                      .stream()
                                      .map(category -> new CategoryJsonResponse(category.getId().toString(),
                                                                                category.getName(),
                                                                                category.getImg())).collect(Collectors.toList()),
                              categoriesPage.getPageable(),
                              categoriesPage.getTotalElements());
    }

    /**
     * Create category.
     *
     * @param categoryJson category json
     * @return category name created
     */
    public String creatCategoryAndGetCategoryName(final CategoryJsonRequest categoryJson) {
        categoryRepository.findCategoryByNameIgnoreCase(categoryJson.getName()).
                ifPresent(s -> {
                    throw new CategoryAlreadyExistsException();
                });
        final Category category = Category.builder()
                .name(categoryJson.getName())
                .img(categoryJson.getImg())
                .createdAt(LocalDate.now())
                .build();
        final var savedCategory = categoryRepository.save(category);
        LOGGER.info("Category is saved with category Id: {}", savedCategory.getId());
        return savedCategory.getName();
    }

    public void deleteCategory(final String categoryId) {
        categoryRepository.deleteById(UUID.fromString(categoryId));
    }

    public void updateCategoryAndGetCategoryName(final CategoryJsonRequest categoryJson) {
        Category category = categoryRepository.findById(UUID.fromString(categoryJson.getId()))
                                                                .orElseThrow(IllegalArgumentException::new);
        category.setName(categoryJson.getName());
        category.setImg(categoryJson.getImg());
        final var savedCategory = categoryRepository.save(category);
        LOGGER.info("Category with id: {} is updated with name: {}", savedCategory.getId(), categoryJson.getName());
    }
}
