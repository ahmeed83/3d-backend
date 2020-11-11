package com.baghdadfocusit.webshop3d.service;

import com.baghdadfocusit.webshop3d.model.product.ProductJsonRequest;
import com.baghdadfocusit.webshop3d.model.product.ProductJsonResponse;
import com.baghdadfocusit.webshop3d.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class ProductServiceTest {

    /**
     * Random UUID.
     */
    private final UUID randomUUID = UUID.randomUUID();

    /**
     * Mocked repository
     */
    @Mock
    ProductRepository productRepository;

    /**
     * Mocked service
     */
    @InjectMocks
    ProductService productService;

    /**
     * Tested entity
     */
    private ProductJsonRequest productJson;

    @BeforeEach
    void setUp() {
        //    List<Product> products = new ArrayList<>();
        //    Page<Product> pagedResponse = new PageImpl(products);
        //    productJson = ProductJson.builder().name("iPhone").price("$200").picLocation("location").build();
        //    when(productRepository.save(any(Product.class))).thenReturn(productJson);
        //    when(productRepository.getFilterProducts(anyString(), anyString(), any())).thenReturn(pagedResponse);
        //    when(productRepository.findById(randomUUID)).thenReturn(java.util.Optional.ofNullable(productJson));
    }

    @Test
    void createNewProduct() {
        final var newProduct = productService.createProductAndGetProductName(productJson);
        //    assertNotNull(newProduct);
        //    assertNull(newProduct.getUpdatedAt());
        //    assertEquals("iPhone", newProduct.getName());
        //    assertEquals(LocalDate.now(), newProduct.getCreatedAt());
    }

//    @Test
//    void getAllProducts() {
//        final var newProducts = (List<ProductJsonResponse>) productService.getFilterProductsByCategoryId(Optional.empty(),
//                                                                                                         Optional.empty(),
//                                                                                                         Optional.empty(),
//                                                                                                         Optional.empty());
//        assertNotNull(newProducts);
//        assertEquals(1, newProducts.size());
//        //        assertNull(newProducts.get(0).getUpdatedAt());
//        assertEquals("iPhone", newProducts.get(0).getName());
//    }

    @Test
    void deleteProduct() {
        productService.deleteProduct(String.valueOf(randomUUID));
        verify(productRepository, times(1)).deleteById(eq(randomUUID));
    }
}
