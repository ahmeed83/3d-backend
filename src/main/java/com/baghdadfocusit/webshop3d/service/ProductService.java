package com.baghdadfocusit.webshop3d.service;

import com.baghdadfocusit.webshop3d.entities.Product;
import com.baghdadfocusit.webshop3d.exception.product.ProductAlreadyExistsException;
import com.baghdadfocusit.webshop3d.exception.product.ProductNotFoundException;
import com.baghdadfocusit.webshop3d.model.category.CategoryJsonResponse;
import com.baghdadfocusit.webshop3d.model.common.ImageJsonResponse;
import com.baghdadfocusit.webshop3d.model.product.ProductJsonRequest;
import com.baghdadfocusit.webshop3d.model.product.ProductJsonResponse;
import com.baghdadfocusit.webshop3d.model.product.ProductUpdatePriceRequest;
import com.baghdadfocusit.webshop3d.repository.ImageRepository;
import com.baghdadfocusit.webshop3d.repository.ProductRepository;
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
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    private final ImageRepository imageRepository;

    private final ImageAwsS3Saver imageAwsS3Saver;

    /**
     * Get All Filtered products.
     *
     * @return products
     */
    public Page<ProductJsonResponse> getAllFilteredProducts(Optional<Integer> page, Optional<String> sortBy) {
        Page<Product> productPage;
        if (sortBy.isPresent()) {
            productPage = productRepository.findAll(
                    PageRequest.of(page.orElse(0), 15, Sort.Direction.ASC, sortBy.orElse("name")));
        } else {
            productPage = productRepository.findAll(PageRequest.of(page.orElse(0), 15, Sort.unsorted()));
        }
        return buildProductJsonResponses(productPage);
    }

    /**
     * Delete one product
     *
     * @param productId productId
     */
    public void deleteProduct(String productId) {
        Product product = productRepository.findById(UUID.fromString(productId))
                .orElseThrow(ProductNotFoundException::new);
        productRepository.deleteById(product.getId());
    }

    /**
     * Get Product by cateogry ID
     *
     * @param categoryId categoryId
     * @param page       page
     * @param sortBy     sortBy
     * @return Page<ProductJsonResponse>
     */
    public Page<ProductJsonResponse> getProductsByCategoryId(Optional<String> categoryId, Optional<Integer> page,
                                                             Optional<String> sortBy) {
        Page<Product> productPage;
        if (sortBy.isPresent()) {
            productPage = productRepository.findProductsByCategory_Id(UUID.fromString(categoryId.orElse("_")),
                                                                      PageRequest.of(page.orElse(0), 10,
                                                                                     Sort.Direction.ASC,
                                                                                     sortBy.orElse("name")));
        } else {
            productPage = productRepository.findProductsByCategory_Id(UUID.fromString(categoryId.orElse("_")),
                                                                      PageRequest.of(page.orElse(0), 10,
                                                                                     Sort.unsorted()));
        }
        return buildProductJsonResponses(productPage);
    }

    /**
     * Get Only Recommended products.
     *
     * @return list of recommended products
     */
    public List<ProductJsonResponse> getRecommendedProducts() {
        return productRepository.findProductsByRecommendedTrue()
                .stream()
                .map(product -> new ProductJsonResponse(product.getId(), product.getName(), product.getPrice(),
                                                        product.getDescription(), product.getOldPrice(),
                                                        product.isSale(), product.isRecommended(),
                                                        product.isOutOfStock(),
                                                        imageRepository.findImagesByProduct_Id(product.getId())
                                                                .stream()
                                                                .map(image -> ImageJsonResponse.builder()
                                                                        .id(String.valueOf(image.getId()))
                                                                        .productImage(image.getPicLocation())
                                                                        .build())
                                                                .collect(Collectors.toList()), product.getPicLocation(),
                                                        CategoryJsonResponse.builder()
                                                                .id(String.valueOf(product.getCategoryId()))
                                                                .build()))
                .collect(Collectors.toList());
    }

    /**
     * Get Only Recommended products pagable.
     *
     * @return list of recommended products
     */
    public Page<ProductJsonResponse> getFilteredRecommendedProducts(Optional<Integer> page, Optional<String> sortBy) {
        Page<Product> productPage;
        if (sortBy.isPresent()) {
            productPage = productRepository.findProductsByRecommendedTrue(
                    PageRequest.of(page.orElse(0), 15, Sort.Direction.ASC, sortBy.orElse("name")));
        } else {
            productPage = productRepository.findProductsByRecommendedTrue(
                    PageRequest.of(page.orElse(0), 15, Sort.unsorted()));
        }
        return buildProductJsonResponses(productPage);
    }

    /**
     * Create one products
     *
     * @param productRequest productRequest
     */
    public void createProduct(ProductJsonRequest productRequest) {
        //TODO:final String imageLink = imageAwsS3Saver.saveImageInAmazonAndGetLink(productRequest.getProductImage());
        productRepository.findProductByNameIgnoreCase(productRequest.getProductName()).
                ifPresent(s -> {
                    throw new ProductAlreadyExistsException();
                });
        final String imageLink = "imageLink";
        final Product product = Product.builder()
                .createdAt(LocalDate.now())
                .name(productRequest.getProductName())
                .categoryId(UUID.fromString(productRequest.getCategoryId()))
                .price(productRequest.getProductPrice())
                .oldPrice(productRequest.getProductOldPrice())
                .outOfStock(productRequest.isOutOfStock())
                .sale(false)
                .recommended(productRequest.isRecommended())
                .description(productRequest.getDescription())
                .picLocation(imageLink)
                .build();
        final var savedProduct = productRepository.save(product);
        LOGGER.info("Product is saved with product Id: {}", savedProduct.getId());
    }

    /**
     * Edit one products
     *
     * @param productRequest productRequest
     */
    public void editProduct(final ProductJsonRequest productRequest) {
        Product product = productRepository.findById(UUID.fromString(productRequest.getId()))
                .orElseThrow(ProductNotFoundException::new);

        if (!product.getName().equals(productRequest.getProductName())) {
            productRepository.findProductByNameIgnoreCase(productRequest.getProductName()).
                    ifPresent(s -> {
                        throw new ProductAlreadyExistsException();
                    });
        }

        //TODO:final String imageLink = imageAwsS3Saver.saveImageInAmazonAndGetLink(productRequest.getProductImage());
        final String imageLink = "imageLink";
        product.setName(productRequest.getProductName());
        product.setRecommended(productRequest.isRecommended());
        product.setCategoryId(UUID.fromString(productRequest.getCategoryId()));
        product.setPrice(productRequest.getProductPrice());
        product.setOldPrice(productRequest.getProductOldPrice());
        product.setSale(productRequest.isSale());
        product.setOutOfStock(productRequest.isOutOfStock());
        product.setDescription(productRequest.getDescription());
        product.setPicLocation(imageLink);
        product.setUpdatedAt(LocalDate.now());

        productRepository.save(product);
        LOGGER.info("Product is updated for product with product id {} ", product.getId());
    }


    /**
     * Update product price.
     *
     * @param productId          productId
     * @param updatePriceRequest updatePriceRequest
     */
    public void updateProductPrice(final String productId, final ProductUpdatePriceRequest updatePriceRequest) {
        Product product = productRepository.findById(UUID.fromString(productId))
                .orElseThrow(ProductNotFoundException::new);
        product.setPrice(updatePriceRequest.getProductPrice());
        product.setUpdatedAt(LocalDate.now());
        productRepository.save(product);
        LOGGER.info("Price is updated for product with product id {} ", product.getId());
    }

    /**
     * Make one product recommended. If it was already recommended, make it not recommended.
     *
     * @param productId productId
     */
    public void makeRecommended(final String productId) {
        Product product = productRepository.findById(UUID.fromString(productId))
                .orElseThrow(ProductNotFoundException::new);
        product.setRecommended(!product.isRecommended());
        product.setUpdatedAt(LocalDate.now());
        productRepository.save(product);
        LOGGER.info("Recommended statue is updated for product with product id {} ", product.getId());
    }

    /**
     * Make one product out of stock. If it was already out of stock, make it not out of stock.
     *
     * @param productId productId
     */
    public void makeOutOfStockRecommended(final String productId) {
        Product product = productRepository.findById(UUID.fromString(productId))
                .orElseThrow(ProductNotFoundException::new);
        product.setOutOfStock(!product.isOutOfStock());
        product.setUpdatedAt(LocalDate.now());
        productRepository.save(product);
        LOGGER.info("Out of stock is updated for product with product id {} ", product.getId());
    }

    /**
     * Build product Json reponse.
     *
     * @param productPage productPage
     * @return ProductJsonResponses
     */
    private Page<ProductJsonResponse> buildProductJsonResponses(final Page<Product> productPage) {
        return new PageImpl<>(productPage.getContent().stream().map(product -> {

            List<ImageJsonResponse> imagesByProductId = imageRepository.findImagesByProduct_Id(product.getId())
                    .stream()
                    .map(image -> ImageJsonResponse.builder()
                            .id(String.valueOf(image.getId()))
                            .productImage(image.getPicLocation())
                            .build())
                    .collect(Collectors.toList());

            return new ProductJsonResponse(product.getId(), product.getName(), product.getPrice(),
                                           product.getDescription(), product.getOldPrice(), product.isSale(),
                                           product.isRecommended(), product.isOutOfStock(), imagesByProductId,
                                           product.getPicLocation(), CategoryJsonResponse.builder()
                                                   .id(String.valueOf(product.getCategoryId()))
                                                   .name(product.getCategory().getName())
                                                   .build());
        }).collect(Collectors.toList()), productPage.getPageable(), productPage.getTotalElements());
    }

    public Page<ProductJsonResponse> searchProductByName(final Optional<String> productName,
                                                         final Optional<Integer> page, final Optional<String> sortBy) {
        Page<Product> productPage;
        if (sortBy.isPresent()) {
            productPage = productRepository.findProductsByNameContainingIgnoreCase(productName.orElse("_"),
                                                                                   PageRequest.of(page.orElse(0), 1000,
                                                                                                  Sort.Direction.ASC,
                                                                                                  sortBy.orElse(
                                                                                                          "name")));
        } else {
            productPage = productRepository.findProductsByNameContainingIgnoreCase(productName.orElse("_"),
                                                                                   PageRequest.of(page.orElse(0), 1000,
                                                                                                  Sort.unsorted()));
        }
        return buildProductJsonResponses(productPage);
    }
}
