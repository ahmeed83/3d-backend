package com.baghdadfocusit.webshop3d.repository;

import com.baghdadfocusit.webshop3d.entities.Category;
import com.baghdadfocusit.webshop3d.entities.Product;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
public class ProductDataTest {

  @Autowired
  private ProductRepository productRepository;

  @BeforeEach
  void setUp() {
    productRepository.save(Product.builder()
                                  .createdAt(LocalDateTime.now())
                                  .name("iPhone")
                                  .price(200)
                                  .picLocation("location")
                                  .category(Category.builder().createdAt(LocalDateTime.now()).name("MacBook").build())
                                  .build());
  }

//  @Test
  void getFilteredProducts() {
    Page<Product> products = productRepository.getFilterProducts("iPhone", "MacBook", Mockito.any(PageRequest.class));
    assertNotNull(products);
//    assertEquals(1, products.size());
//    assertEquals("iPhone", products.get(0).getName());
  }
}
