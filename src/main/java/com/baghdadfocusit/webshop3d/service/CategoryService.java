package com.baghdadfocusit.webshop3d.service;

import com.baghdadfocusit.webshop3d.entities.Category;
import com.baghdadfocusit.webshop3d.entities.SubCategory;
import com.baghdadfocusit.webshop3d.exception.category.CategoryAlreadyExistsException;
import com.baghdadfocusit.webshop3d.exception.category.CategoryNotFoundException;
import com.baghdadfocusit.webshop3d.exception.category.SubCategoryAlreadyExistsException;
import com.baghdadfocusit.webshop3d.model.CategoryJsonResponse;
import com.baghdadfocusit.webshop3d.model.SubCategoryJson;
import com.baghdadfocusit.webshop3d.repository.CategoryRepository;
import com.baghdadfocusit.webshop3d.repository.SubCategoryRepository;
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
    private final SubCategoryRepository subCategoryRepository;

    /**
     * Constructor.
     *
     * @param categoryRepository    categoryRepository
     * @param subCategoryRepository subCategoryRepository
     */
    public CategoryService(CategoryRepository categoryRepository, final SubCategoryRepository subCategoryRepository) {
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
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
     * Get all sub categories based on the category id.
     *
     * @param categoryId categoryId
     * @return sub categories
     */
    public List<SubCategoryJson> getAllSubCategories(final String categoryId) {
        final List<SubCategory> subCategoriesByCategoryIdList;
        try {
            subCategoriesByCategoryIdList = subCategoryRepository.findSubCategoriesByCategoryId(
                    UUID.fromString(categoryId));
            if (subCategoriesByCategoryIdList.isEmpty()) {
                return List.of(new SubCategoryJson(UUID.randomUUID(), "No sub categories found", "categoryId"));
            }
            return subCategoriesByCategoryIdList.stream()
                    .map(subCategory -> new SubCategoryJson(subCategory.getId(), subCategory.getName(),
                                                            String.valueOf(subCategory.getCategoryId())))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of(new SubCategoryJson(UUID.randomUUID(), "No Category selected", "categoryId"));
        }
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

    /**
     * Create sub category.
     *
     * @param subCategoryJson subCategoryJson
     * @return sub category name
     */
    public String creatSubCategoryAndGetSubCategoryName(final SubCategoryJson subCategoryJson) {
        subCategoryRepository.findSubCategoryByNameIgnoreCase(subCategoryJson.getSubCategoryName()).
                ifPresent(s -> {
                    throw new SubCategoryAlreadyExistsException();
                });
        final Category category = categoryRepository.findById(UUID.fromString(subCategoryJson.getCategoryId()))
                .orElseThrow(CategoryNotFoundException::new);
        final SubCategory subCategory = SubCategory.builder()
                .categoryId(category.getId())
                .category(category)
                .name(subCategoryJson.getSubCategoryName())
                .createdAt(LocalDate.now())
                .build();
        final var savedSubCategory = subCategoryRepository.save(subCategory);
        LOGGER.info("Sub Category is saved with sub category Id: {}", savedSubCategory.getId());
        return savedSubCategory.getName();
    }
}
