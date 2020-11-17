package com.baghdadfocusit.webshop3d.service;

import com.baghdadfocusit.webshop3d.entities.Category;
import com.baghdadfocusit.webshop3d.exception.category.CategoryAlreadyExistsException;
import com.baghdadfocusit.webshop3d.exception.category.CategoryNotFoundException;
import com.baghdadfocusit.webshop3d.model.category.CategoryJsonRequest;
import com.baghdadfocusit.webshop3d.model.category.CategoryJsonResponse;
import com.baghdadfocusit.webshop3d.repository.CategoryRepository;
import com.baghdadfocusit.webshop3d.service.util.ImageAwsS3Saver;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CategoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;
    private final ImageAwsS3Saver imageAwsS3Saver;


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
        Page<Category> categoriesPage;
        if (sortBy.isPresent()) {
            categoriesPage = categoryRepository.findAll(
                    PageRequest.of(page.orElse(0), 25, Sort.Direction.ASC, sortBy.orElse("name")));
        } else {
            categoriesPage = categoryRepository.findAll(PageRequest.of(page.orElse(0), 15, Sort.unsorted()));
        }
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
        //TODO:final String imageLink = imageAwsS3Saver.saveImageInAmazonAndGetLink(categoryRequest.getImg());
        final String imageLink = "imageLink";
        categoryRepository.findCategoryByNameIgnoreCase(categoryRequest.getName()).
                ifPresent(s -> {
                    throw new CategoryAlreadyExistsException();
                });
        final Category category = Category.builder()
                .name(categoryRequest.getName())
                .img(imageLink)
                .createdAt(LocalDate.now())
                .build();
        final var savedCategory = categoryRepository.save(category);
        LOGGER.info("Category is saved with category Id: {}", savedCategory.getId());
    }

    public void deleteCategory(final String categoryId) {
        categoryRepository.deleteById(UUID.fromString(categoryId));
    }

    public void editCategory(final CategoryJsonRequest categoryRequest) {
        //TODO:final String imageLink = imageAwsS3Saver.saveImageInAmazonAndGetLink(categoryRequest.getImg());
        final String imageLink = "imageLink";
        categoryRepository.findCategoryByNameIgnoreCase(categoryRequest.getName()).
                ifPresent(s -> {
                    throw new CategoryAlreadyExistsException();
                });
        Category category = categoryRepository.findById(UUID.fromString(categoryRequest.getId()))
                .orElseThrow(CategoryNotFoundException::new);
        category.setName(categoryRequest.getName());
        category.setImg(imageLink);
        final var savedCategory = categoryRepository.save(category);
        LOGGER.info("Category with id: {} is updated with name: {}", savedCategory.getId(), categoryRequest.getName());
    }
}
