package com.baghdadfocusit.webshop3d.service;

import com.baghdadfocusit.webshop3d.entities.Category;
import com.baghdadfocusit.webshop3d.exception.category.CategoryAlreadyExistsException;
import com.baghdadfocusit.webshop3d.exception.category.CategoryNotFoundException;
import com.baghdadfocusit.webshop3d.model.category.CategoryJsonRequest;
import com.baghdadfocusit.webshop3d.model.category.CategoryJsonResponse;
import com.baghdadfocusit.webshop3d.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;
    private final ImageAwsS3Service imageAwsS3Saver;
    private static final String IMAGE_TYPE_NAME = "category";

    /**
     * Get all categories.
     *
     * @return categories
     */
    public List<CategoryJsonResponse> getAllCategories() {
        List<Category> categories = (List<Category>) categoryRepository.findAll();
        return categories.stream()
                .map(category -> new CategoryJsonResponse(category.getId().toString(), category.getName(),
                                                          category.getImg()))
                .collect(Collectors.toList());
    }

    /**
     * Get Filtered categories.
     *
     * @return categories
     */
    public Page<CategoryJsonResponse> getFilterCategories(Optional<Integer> page, Optional<String> sortBy) {
        Page<Category> categoriesPage = categoryRepository.findAll(
                PageRequest.of(page.orElse(0), 15, Sort.by("updatedAt").descending()));

        return new PageImpl<>(categoriesPage.getContent()
                                      .stream()
                                      .map(category -> new CategoryJsonResponse(category.getId().toString(),
                                                                                category.getName(), category.getImg()))
                                      .collect(Collectors.toList()), categoriesPage.getPageable(),
                              categoriesPage.getTotalElements());
    }

    /**
     * Create category.
     *
     * @param categoryRequest category json
     * @return category name created
     */
    public void creatCategory(final CategoryJsonRequest categoryRequest) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(TimeZone.getTimeZone("Asia/Baghdad").toZoneId());
        categoryRepository.findCategoryByNameIgnoreCase(categoryRequest.getCategoryName()).
                ifPresent(s -> {
                    throw new CategoryAlreadyExistsException();
                });
        final Category category = Category.builder()
                .name(categoryRequest.getCategoryName())
                .img(imageAwsS3Saver.saveImageInAmazonAndGetLink(categoryRequest.getCategoryImage(), IMAGE_TYPE_NAME))
                .createdAt(LocalDateTime.from(zonedDateTime))
                .updatedAt(LocalDateTime.from(zonedDateTime))
                .build();
        final var savedCategory = categoryRepository.save(category);
        LOGGER.info("Category is saved with category Id: {}", savedCategory.getId());
    }

    public void deleteCategory(final String categoryId) {
        categoryRepository.deleteById(UUID.fromString(categoryId));
    }

    /**
     * find if the category in the database
     * if the name is not the same, then the user want to change it, so search if the changed one
     * already exists in the database.
     * if there is an image, save it, otherwise don't do anything.
     * save the name
     *
     * @param categoryRequest categoryRequest
     */
    public void editCategory(final CategoryJsonRequest categoryRequest) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(TimeZone.getTimeZone("Asia/Baghdad").toZoneId());
        Category category = categoryRepository.findById(UUID.fromString(categoryRequest.getId()))
                .orElseThrow(CategoryNotFoundException::new);
        if (!category.getName().equals(categoryRequest.getCategoryName())) {
            categoryRepository.findCategoryByNameIgnoreCase(categoryRequest.getCategoryName()).
                    ifPresent(s -> {
                        throw new CategoryAlreadyExistsException();
                    });
        }
        if (categoryRequest.getCategoryImage() != null && !categoryRequest.getCategoryImage().isEmpty()) {
            imageAwsS3Saver.deleteImage(category.getImg());
            category.setImg(
                    imageAwsS3Saver.saveImageInAmazonAndGetLink(categoryRequest.getCategoryImage(), IMAGE_TYPE_NAME));
        }
        category.setName(categoryRequest.getCategoryName());
        category.setUpdatedAt(LocalDateTime.from(zonedDateTime));
        final var savedCategory = categoryRepository.save(category);
        LOGGER.info("Category with id: {} is updated with name: {}", savedCategory.getId(),
                    categoryRequest.getCategoryName());
    }
}
