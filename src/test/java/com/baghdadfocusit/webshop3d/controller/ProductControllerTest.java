package com.baghdadfocusit.webshop3d.controller;

import com.baghdadfocusit.webshop3d.entities.Product;
import com.baghdadfocusit.webshop3d.service.ProductService;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductControllerTest {

  /**
   * Path to management endpoints
   */
  private static final String ADD_PRODUCTS_PATH = "/resources/products";

  /**
   * Tested class
   */
  private Product product;

  /**
   * Product service mock
   */
  @MockBean
  private ProductService productService;

  /**
   * Mock MVC
   */
  @Autowired
  private MockMvc mockMvc;

//  @BeforeEach
//  void setUp() {
//    product = Product.builder().createdAt(LocalDate.now()).name("iPhone").price(200).picLocation("location").build();
//    when(productService.getFilterProductsByCategoryId(Optional.empty(),
//                                                      Optional.empty(),
//                                                      Optional.empty(),
//                                                      Optional.empty())).thenReturn((Page) Arrays.asList(product));
//  }

  @Test
  @DisplayName("0- Test GET anyone can get the list of the products")
  void getAllProductsOGet() throws Exception {
    this.mockMvc.perform(get(ADD_PRODUCTS_PATH))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().string(containsString("iPhone")));
  }

  @Test
  @DisplayName("1- Test POST NO one can post to products")
  void postProductsNotOkPost() throws Exception {
    this.mockMvc.perform(post(ADD_PRODUCTS_PATH))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
  }


  @Test
  @DisplayName("2- Test DELETE NO one can delete from products")
  void postProductsNotOkDelete() throws Exception {
    this.mockMvc.perform(delete(ADD_PRODUCTS_PATH))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
  }


  @Test
  @DisplayName("3- Test PUT NO can change products details")
  void postProductsNotOkPut() throws Exception {
    this.mockMvc.perform(put(ADD_PRODUCTS_PATH))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
  }
}
