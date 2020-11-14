package com.baghdadfocusit.webshop3d.service;

import com.baghdadfocusit.webshop3d.configuration.aws.AmazonFileStore;
import com.baghdadfocusit.webshop3d.entities.Product;
import com.baghdadfocusit.webshop3d.model.category.CategoryJsonResponse;
import com.baghdadfocusit.webshop3d.model.product.ProductJsonRequest;
import com.baghdadfocusit.webshop3d.model.product.ProductJsonResponse;
import com.baghdadfocusit.webshop3d.model.product.ProductUpdatePriceRequest;
import com.baghdadfocusit.webshop3d.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.apache.http.entity.ContentType.IMAGE_GIF;
import static org.apache.http.entity.ContentType.IMAGE_JPEG;
import static org.apache.http.entity.ContentType.IMAGE_PNG;

@Service
@RequiredArgsConstructor
public class ProductService {

    @Value("${aws.s3.bucket}")
    private String bucket;
    private final ProductRepository productRepository;
    private final AmazonFileStore amazonFileStore;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    
    /**
     * Get All Filtered products.
     *
     * @return products
     */
    public Page<ProductJsonResponse> getAllFilteredProducts(Optional<Integer> page, Optional<String> sortBy) {
        Page<Product> productPage;
        if (sortBy.isPresent()) {
            productPage = productRepository.findAll(PageRequest.of(page.orElse(0), 15, Sort.Direction.ASC,
                                                                   sortBy.orElse("name")));
        } else {
            productPage = productRepository.findAll(PageRequest.of(page.orElse(0), 15, Sort.unsorted()));
        }
        return new PageImpl<>(productPage.getContent()
                                      .stream()
                                      .map(product -> new ProductJsonResponse(product.getId(), product.getName(), 
                                                                              product.getPrice(), product.isSale(),
                                                                              product.isRecommended(), product.getPicLocation(), 
                                                                              product.getDescription(), product.getQuantity(),
                                                                              CategoryJsonResponse.builder()
                                                                                      .id(String.valueOf(product.getCategoryId()))
                                                                                      .name(product.getCategory().getName())
                                                                                      .build())).collect(Collectors.toList()),
                              productPage.getPageable(),
                              productPage.getTotalElements());
    }

    /**
     * Delete one product
     * @param productId productId
     */
    public void deleteProduct(String productId) {
        productRepository.deleteById(UUID.fromString(productId));
    }

    /**
     * Get Product by cateogry ID
     * @param categoryId categoryId
     * @param page page
     * @param sortBy sortBy
     * @return Page<ProductJsonResponse>
     */
    public Page<ProductJsonResponse> getProductsByCategoryId(Optional<String> categoryId,
                                                             Optional<Integer> page,
                                                             Optional<String> sortBy) {
        return new PageImpl<>(productRepository.findProductsByCategory_Id(UUID.fromString(categoryId.orElse("_")),
                                                           PageRequest.of(page.orElse(0), 5, Direction.ASC,
                                                                          sortBy.orElse("name")))
                .stream().map(product -> new ProductJsonResponse(product.getId(), product.getName(),
                                                                product.getPrice(), product.isSale(),
                                                                product.isRecommended(), product.getPicLocation(),
                                                                product.getDescription(), product.getQuantity(), 
                                                                 CategoryJsonResponse.builder()
                                                                         .id(String.valueOf(product.getCategoryId()))
                                                                         .build()))
                .collect(Collectors.toList()));
    }

    /**
     * Get Only Recommended products.
     *
     * @return list of recommended products
     */
    public List<ProductJsonResponse> getRecommendedProducts() {
        return productRepository.findProductsByRecommendedTrue()
                .stream().map(product -> new ProductJsonResponse(product.getId(), product.getName(),
                                                                 product.getPrice(), product.isSale(),
                                                                 product.isRecommended(), product.getPicLocation(),
                                                                 product.getDescription(), product.getQuantity(),
                                                                 CategoryJsonResponse.builder()
                                                                         .id(String.valueOf(product.getCategoryId()))
                                                                         .build()))
                                                .collect(Collectors.toList());
    }

    /**
     * Create one products
     *
     * @param productRequest productRequest
     */
    public void createProduct(ProductJsonRequest productRequest) {
        //TODO: final String imageLink = saveImageInAmazonAndGetLink(productRequest.getProductImage());
        final String imageLink = "hello";
        final Product product = Product.builder().createdAt(LocalDate.now())
                                                 .name(productRequest.getProductName())
                                                 .price(productRequest.getProductPrice())
                                                 .picLocation(imageLink)
                                                 .quantity(productRequest.getQuantity())
                                                 .sale(false)
                                                 .recommended(productRequest.isRecommended())
                                                 .description(productRequest.getDescription())
                                                 .categoryId(UUID.fromString(productRequest.getCategoryId()))
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
                .orElseThrow(() -> new IllegalArgumentException("No Product found!"));

        //TODO: final String imageLink = saveImageInAmazonAndGetLink(productRequest.getProductImage());
        final String imageLink = "hello";
        product.setName(productRequest.getProductName());
        product.setRecommended(productRequest.isRecommended());
        product.setCategoryId(UUID.fromString(productRequest.getCategoryId()));
        product.setPrice(productRequest.getProductPrice());
        product.setSale(productRequest.isSale());
        product.setDescription(productRequest.getDescription());
        product.setQuantity(productRequest.getQuantity());
        product.setPicLocation(imageLink);
        product.setUpdatedAt(LocalDate.now());

        productRepository.save(product);
        LOGGER.info("Price is updated for product with product id {} ", product.getId());
    }


    /**
     * Update product price.
     *
     * @param productId productId
     * @param updatePriceRequest updatePriceRequest
     */
    public void updateProductPrice(final String productId, final ProductUpdatePriceRequest updatePriceRequest) {
        Product product = productRepository.findById(UUID.fromString(productId))
                .orElseThrow(() -> new IllegalArgumentException("No Product found!"));
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
                .orElseThrow(() -> new IllegalArgumentException("No Product found!"));
        product.setRecommended(!product.isRecommended());
        product.setUpdatedAt(LocalDate.now());
        productRepository.save(product);
        LOGGER.info("Recommended statue is updated for product with product id {} ", product.getId());
    }

    /**
     * Save product image
     *
     * @param productImage productImage
     * @return link of the image
     */
    private String saveImageInAmazonAndGetLink(final MultipartFile productImage) {
        isImage(productImage);
        final Map<String, String> metadata = getMetaData(productImage);
        final String path = String.format("%s", bucket);
        final String fileName = String.format("%s-%s", UUID.randomUUID(), LocalDateTime.now());
        try {
            LOGGER.info("Uploading image with name= " + fileName);
            amazonFileStore.saveImageInAmazon(path, fileName, Optional.of(metadata), productImage.getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return amazonFileStore.getImageUrl(bucket, fileName);
    }

    private Map<String, String> getMetaData(final MultipartFile productImage) {
        final Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", productImage.getContentType());
        metadata.put("Content-Length", String.valueOf(productImage.getSize()));
        return metadata;
    }

    private void isImage(final MultipartFile productImage) {
        if (!Arrays.asList(IMAGE_JPEG.getMimeType(), IMAGE_PNG.getMimeType(), IMAGE_GIF.getMimeType())
                .contains(productImage.getContentType())) {
            throw new IllegalStateException("File must be an Image [" + productImage.getContentType() + "]");
        }
    }
}
