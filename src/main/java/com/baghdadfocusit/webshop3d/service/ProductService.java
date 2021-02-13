package com.baghdadfocusit.webshop3d.service;

import com.baghdadfocusit.webshop3d.entities.Image;
import com.baghdadfocusit.webshop3d.entities.Product;
import com.baghdadfocusit.webshop3d.exception.product.ProductAlreadyExistsException;
import com.baghdadfocusit.webshop3d.exception.product.ProductNotFoundException;
import com.baghdadfocusit.webshop3d.model.category.CategoryJsonResponse;
import com.baghdadfocusit.webshop3d.model.common.ImageJsonResponse;
import com.baghdadfocusit.webshop3d.model.product.ProductHomePageImageJsonResponse;
import com.baghdadfocusit.webshop3d.model.product.ProductJsonRequest;
import com.baghdadfocusit.webshop3d.model.product.ProductJsonResponse;
import com.baghdadfocusit.webshop3d.model.product.ProductUpdatePriceRequest;
import com.baghdadfocusit.webshop3d.repository.ImageRepository;
import com.baghdadfocusit.webshop3d.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final ImageAwsS3Service imageAwsS3Service;
    private static final String IMAGE_TYPE_NAME_PRODUCT = "product";
    private static final String IMAGE_TYPE_NAME_HOME_SCREEN = "home-screen";

    /**
     * Get All Filtered products.
     *
     * @return products
     */
    public Page<ProductJsonResponse> getAllFilteredProducts(Optional<Integer> page, Optional<String> sortBy) {
        Page<Product> productPage = productRepository.findAll(
                PageRequest.of(page.orElse(0), 15, Sort.by("updatedAt").descending()));
        return buildProductJsonResponses(productPage);
    }

    /**
     * Get All Home page product Images.
     *
     * @return products
     */
    public List<ProductHomePageImageJsonResponse> getProductsHomePageImages() {
        List<Product> productPage = productRepository.findProductsByHomeScreenPicLocationNotNull();
        return productPage.stream()
                .map(product -> ProductHomePageImageJsonResponse.builder()
                        .productId(product.getId())
                        .productHomeScreenPicLocation(product.getHomeScreenPicLocation())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Get one product
     *
     * @param productId productId
     * @return product
     */
    public ProductJsonResponse getProductsById(final String productId) {
        Product product = productRepository.findById(UUID.fromString(productId))
                .orElseThrow(ProductNotFoundException::new);
        return buildProductJsonResponse(product);
    }

    /**
     * Delete home screen image for product
     *
     * @param productId productId
     */
    @Transactional
    public void deleteHomeScreenImageForProduct(String productId) {
        Product product = productRepository.findById(UUID.fromString(productId))
                .orElseThrow(ProductNotFoundException::new);
        productRepository.deleteHomeScreenImageForProduct(product.getId());
        imageAwsS3Service.deleteImage(product.getHomeScreenPicLocation());
        LOGGER.info("Home screen Image for Product {} had been deleted", productId);
    }

    /**
     * Delete one product
     *
     * @param productId productId
     */
    @Transactional
    public void deleteProduct(String productId) {
        Product product = productRepository.findById(UUID.fromString(productId))
                .orElseThrow(ProductNotFoundException::new);

        imageAwsS3Service.deleteImage(product.getPicLocation());
        if (product.getHomeScreenPicLocation() != null) {
            imageAwsS3Service.deleteImage(product.getHomeScreenPicLocation());
        }
        imageRepository.findImagesByProduct_Id(product.getId())
                .forEach(image -> imageAwsS3Service.deleteImage(image.getPicLocation()));

        productRepository.deleteById(product.getId());
        LOGGER.info("Product is delete with product Id: {}", productId);
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
        Page<Product> productPage = productRepository.findProductsByCategory_Id(UUID.fromString(categoryId.orElse("_")),
                                                                                PageRequest.of(page.orElse(0), 12,
                                                                                               Sort.by("updatedAt")
                                                                                                       .descending()));
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
                .map(this::buildProductJsonResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get Only Recommended products pagable.
     *
     * @return list of recommended products
     */
    public Page<ProductJsonResponse> getFilteredRecommendedProducts(Optional<Integer> page, Optional<String> sortBy) {
        Page<Product> productPage;
        productPage = productRepository.findProductsByRecommendedTrue(
                PageRequest.of(page.orElse(0), 15, Sort.by("updatedAt").descending()));
        return buildProductJsonResponses(productPage);
    }

    /**
     * Create one products
     *
     * @param productRequest productRequest
     */
    @Transactional
    public void createProduct(ProductJsonRequest productRequest) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(TimeZone.getTimeZone("Asia/Baghdad").toZoneId());
        productRepository.findProductByNameIgnoreCase(productRequest.getProductName()).
                ifPresent(s -> {
                    throw new ProductAlreadyExistsException();
                });
        final String mainImageLink = imageAwsS3Service.saveImageInAmazonAndGetLink(productRequest.getPicLocation(),
                                                                                   IMAGE_TYPE_NAME_PRODUCT);
        String homeScreenImageLink = null;
        if (productRequest.getHomeScreenPicLocation() != null && !productRequest.getHomeScreenPicLocation().isEmpty()) {
            homeScreenImageLink = imageAwsS3Service.saveImageInAmazonAndGetLink(
                    productRequest.getHomeScreenPicLocation(), IMAGE_TYPE_NAME_HOME_SCREEN);
        }

        final Product product = Product.builder()
                .createdAt(LocalDateTime.from(zonedDateTime))
                .updatedAt(LocalDateTime.from(zonedDateTime))
                .name(productRequest.getProductName())
                .categoryId(UUID.fromString(productRequest.getCategoryId()))
                .price(productRequest.getProductPrice())
                .oldPrice(productRequest.getProductOldPrice())
                .priceAssemble(productRequest.getPriceAssemble())
                .outOfStock(productRequest.isOutOfStock())
                .sale(productRequest.isSale())
                .recommended(productRequest.isRecommended())
                .onlyShopAvailable(productRequest.isOnlyShopAvailable())
                .comingSoon(productRequest.isComingSoon())
                .description(productRequest.getDescription())
                .picLocation(mainImageLink)
                .homeScreenPicLocation(homeScreenImageLink)
                .build();

        final var savedProduct = productRepository.save(product);

        for (MultipartFile image : productRequest.getProductImages()) {
            final String imageLink = imageAwsS3Service.saveImageInAmazonAndGetLink(image, IMAGE_TYPE_NAME_PRODUCT);
            imageRepository.save(Image.builder()
                                         .createdAt(LocalDateTime.from(zonedDateTime))
                                         .updatedAt(LocalDateTime.from(zonedDateTime))
                                         .productId(savedProduct.getId())
                                         .picLocation(imageLink)
                                         .build());
        }
        LOGGER.info("Product is saved with product Id: {}", savedProduct.getId());
    }

    /**
     * Edit one products
     *
     * @param productRequest productRequest
     */
    @Transactional
    public void editProduct(final ProductJsonRequest productRequest) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(TimeZone.getTimeZone("Asia/Baghdad").toZoneId());
        Product product = productRepository.findById(UUID.fromString(productRequest.getId()))
                .orElseThrow(ProductNotFoundException::new);

        if (!product.getName().equals(productRequest.getProductName())) {
            productRepository.findProductByNameIgnoreCase(productRequest.getProductName()).
                    ifPresent(s -> {
                        throw new ProductAlreadyExistsException();
                    });
        }
        product.setName(productRequest.getProductName());
        product.setRecommended(productRequest.isRecommended());
        product.setCategoryId(UUID.fromString(productRequest.getCategoryId()));
        product.setPrice(productRequest.getProductPrice());
        product.setOldPrice(productRequest.getProductOldPrice());
        product.setPriceAssemble(productRequest.getPriceAssemble());
        product.setSale(productRequest.isSale());
        product.setOutOfStock(productRequest.isOutOfStock());
        product.setOnlyShopAvailable(productRequest.isOnlyShopAvailable());
        product.setComingSoon(productRequest.isComingSoon());
        product.setDescription(productRequest.getDescription());
        product.setUpdatedAt(LocalDateTime.from(zonedDateTime));

        if (productRequest.getPicLocation() != null && !productRequest.getPicLocation().isEmpty()) {
            final String mainImageLink = imageAwsS3Service.saveImageInAmazonAndGetLink(productRequest.getPicLocation(),
                                                                                       IMAGE_TYPE_NAME_PRODUCT);
            product.setPicLocation(mainImageLink);
        }

        if (productRequest.getHomeScreenPicLocation() != null && !productRequest.getHomeScreenPicLocation().isEmpty()) {
            final String mainImageLink = imageAwsS3Service.saveImageInAmazonAndGetLink(
                    productRequest.getHomeScreenPicLocation(), IMAGE_TYPE_NAME_HOME_SCREEN);
            product.setHomeScreenPicLocation(mainImageLink);
        }

        final var savedProduct = productRepository.save(product);

        if (productRequest.getProductImages() != null && !productRequest.getProductImages().isEmpty()) {
            imageRepository.findImagesByProduct_Id(product.getId())
                    .forEach(image -> imageAwsS3Service.deleteImage(image.getPicLocation()));

            imageRepository.deleteByProduct_Id(product.getId());

            for (MultipartFile image : productRequest.getProductImages()) {
                final String imageLink = imageAwsS3Service.saveImageInAmazonAndGetLink(image, IMAGE_TYPE_NAME_PRODUCT);
                imageRepository.save(Image.builder()
                                             .createdAt(LocalDateTime.from(zonedDateTime))
                                             .updatedAt(LocalDateTime.from(zonedDateTime))
                                             .productId(savedProduct.getId())
                                             .picLocation(imageLink)
                                             .build());
            }
        }
        LOGGER.info("Product is updated for product with product id {} ", savedProduct.getId());
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
        productRepository.save(product);
        LOGGER.info("Out of stock is updated for product with product id {} ", product.getId());
    }

    /**
     * Make only available on shop. Customers can only buy this products on shop
     *
     * @param productId productId
     */
    public void makeOnlyShopAvailable(final String productId) {
        Product product = productRepository.findById(UUID.fromString(productId))
                .orElseThrow(ProductNotFoundException::new);
        product.setOnlyShopAvailable(!product.isOnlyShopAvailable());
        productRepository.save(product);
        LOGGER.info("Only shop available is updated for product with product id {} ", product.getId());
    }

    /**
     * Build product Json reponse.
     *
     * @param productPage productPage
     * @return ProductJsonResponses
     */
    private Page<ProductJsonResponse> buildProductJsonResponses(final Page<Product> productPage) {
        return new PageImpl<>(
                productPage.getContent().stream().map(this::buildProductJsonResponse).collect(Collectors.toList()),
                productPage.getPageable(), productPage.getTotalElements());
    }

    /**
     * Search Product By name.
     *
     * @param productName productName
     * @return Page ProductJsonResponse
     */
    public Page<ProductJsonResponse> searchProductByName(final Optional<String> productName,
                                                         final Optional<Integer> page, final Optional<String> sortBy) {
        Page<Product> productPage = productRepository.findProductsByNameContainingIgnoreCase(productName.orElse("_"),
                                                                                             PageRequest.of(
                                                                                                     page.orElse(0), 12,
                                                                                                     Sort.by("updatedAt")
                                                                                                             .descending()));
        return buildProductJsonResponses(productPage);
    }

    private ProductJsonResponse buildProductJsonResponse(final Product product) {
        return ProductJsonResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .oldPrice(product.getOldPrice())
                .priceAssemble(product.getPriceAssemble())
                .description(product.getDescription())
                .sale(product.isSale())
                .recommended(product.isRecommended())
                .onlyShopAvailable(product.isOnlyShopAvailable())
                .outOfStock(product.isOutOfStock())
                .comingSoon(product.isComingSoon())
                .picLocation(product.getPicLocation())
                .homeScreenPicLocation(product.getHomeScreenPicLocation())
                .imageJsonResponses(getImageJsonResponse(product.getId()))
                .category(CategoryJsonResponse.builder()
                                  .id(String.valueOf(product.getCategoryId()))
                                  .categoryName(product.getCategory().getName())
                                  .build())
                .build();
    }

    private List<ImageJsonResponse> getImageJsonResponse(final UUID productId) {
        return imageRepository.findImagesByProduct_Id(productId)
                .stream()
                .map(image -> ImageJsonResponse.builder()
                        .id(String.valueOf(image.getId()))
                        .productImageLocation(image.getPicLocation())
                        .build())
                .collect(Collectors.toList());
    }
}