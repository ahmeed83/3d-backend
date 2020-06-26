package com.baghdadfocusit.webshop3d.service;

import com.baghdadfocusit.webshop3d.configuration.aws.AmazonFileStore;
import com.baghdadfocusit.webshop3d.entities.Product;
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

    public Page<ProductJsonResponse> getFilterProducts(Optional<String> name,
                                                       Optional<String> categoryName,
                                                       Optional<Integer> page,
                                                       Optional<String> sortBy) {
        Page<Product> productPage;
        if (sortBy.isPresent()) {
            productPage = productRepository.getFilterProducts(name.orElse("_"), categoryName.orElse("_"),
                                                              PageRequest.of(page.orElse(0), 25, Direction.ASC,
                                                                             sortBy.orElse("name")));
        } else {
            productPage = productRepository.getFilterProducts(name.orElse("_"), categoryName.orElse("_"),
                                                              PageRequest.of(page.orElse(0), 25, Sort.unsorted()));
        }

        return new PageImpl<>(productPage.getContent()
                                      .stream()
                                      .map(product -> new ProductJsonResponse(product.getId(), product.getName(),
                                                                              product.getPrice(), product.isSale(),
                                                                              product.getPicLocation(),
                                                                              product.getDescription(),
                                                                              product.getQuantity(),
                                                                              product.getCategory(),
                                                                              product.getSubCategory()))
                                      .collect(Collectors.toList()), productPage.getPageable(),
                              productPage.getTotalElements());
    }

    public void deleteProduct(String productId) {
        productRepository.deleteById(UUID.fromString(productId));
    }

    public String createProductAndGetProductName(ProductJsonRequest productJson) {
        final String imageLink = saveImageInAmazonAndGetLink(productJson.getProductImage());
        final Product product = Product.builder()
                .createdAt(LocalDate.now())
                .name(productJson.getProductName())
                .price(productJson.getProductPrice())
                .picLocation(imageLink)
                .sale(false)
                .categoryId(UUID.fromString(productJson.getCategoryId()))
                .subCategoryId(UUID.fromString(productJson.getSubCategoryId()))
                .build();
        final var savedProduct = productRepository.save(product);
        LOGGER.info("Product is saved with product Id: {}", savedProduct.getId());
        return String.valueOf(savedProduct.getName());
    }

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

    public void updateProductPrice(final String productId, final ProductUpdatePriceRequest updatePriceRequest) {
        Product product = productRepository.findById(UUID.fromString(productId))
                .orElseThrow(() -> new IllegalArgumentException("No Product found!"));
        product.setPrice(updatePriceRequest.getProductPrice());
        product.setUpdatedAt(LocalDate.now());
        productRepository.save(product);
        LOGGER.info("Price is updated for product with product id {} ", product.getId());
    }
}
