package com.baghdadfocusit.webshop3d.controller.management;

import com.baghdadfocusit.webshop3d.entities.Product;
import com.baghdadfocusit.webshop3d.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class ProductManagementControllerTest {

    /**
     * Random UUID
     */
    public static final UUID TEST_UUID = UUID.randomUUID();

    /**
     * Path to management endpoints
     */
    private static final String MANAGEMENT_ADD_PRODUCTS_PATH = "/management/v1/products";

    /**
     * Tested entity
     */
    private Product product;

    /**
     * Product service
     */
    @MockBean
    private ProductService productService;

    /**
     * Mock mvc
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * ObjectMapper
     */
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .createdAt(LocalDateTime.now())
                .id(TEST_UUID)
                .name("iPhone")
                .price(200)
                .picLocation("location")
                .build();
        //    when(productService.createNewProduct(any(ProductJson.class))).thenReturn(product);
//        when(productService.findProduct(String.valueOf(TEST_UUID))).thenReturn(java.util.Optional.ofNullable(product));
    }

    @Test
    @DisplayName("0- Test GET management products path is not accessible")
    void getAllProductsOGet() throws Exception {
        this.mockMvc.perform(get(MANAGEMENT_ADD_PRODUCTS_PATH)).andDo(print()).andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("1- Test POST management products path is not accessible without ROLES")
    void createProductAnonymous() throws Exception {
        this.mockMvc.perform(post(MANAGEMENT_ADD_PRODUCTS_PATH)).andDo(print()).andExpect(status().isForbidden());
        this.mockMvc.perform(put(MANAGEMENT_ADD_PRODUCTS_PATH + "/{productId}", TEST_UUID))
                .andDo(print())
                .andExpect(status().isForbidden());
        this.mockMvc.perform(delete(MANAGEMENT_ADD_PRODUCTS_PATH + "/{productId}", TEST_UUID))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("2- Test CUD Customer or anyone else does NOT have access to CUD the management path")
    @WithMockUser(username = "name", password = "pwd", roles = {"CUSTOMER", "fhdgvuyeg"})
    void createProductCustomer() throws Exception {
        this.mockMvc.perform(post(MANAGEMENT_ADD_PRODUCTS_PATH).content(objectMapper.writeValueAsString(product))
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
        this.mockMvc.perform(put(MANAGEMENT_ADD_PRODUCTS_PATH + "/{productId}", TEST_UUID).content(
                objectMapper.writeValueAsString(product))
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
        this.mockMvc.perform(delete(MANAGEMENT_ADD_PRODUCTS_PATH + "/{productId}", TEST_UUID))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("3- Test POST only Admin or Employee can ADD products using the management path")
    @WithMockUser(username = "name", password = "pwd", roles = {"EMPLOYEE", "ADMIN"})
    void createProductAdminAndEmployee() throws Exception {
        this.mockMvc.perform(post(MANAGEMENT_ADD_PRODUCTS_PATH).content(objectMapper.writeValueAsString(product))
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.updatedAt").doesNotExist())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.name").value("iPhone"))
                .andExpect(jsonPath("$.picLocation").value("location"))
                .andExpect(jsonPath("$.price").value("$200"));
    }

    @Test
    @DisplayName("3- Test PUT only Admin or Employee can CHANGE products using the management path")
    @WithMockUser(username = "name", password = "pwd", roles = {"EMPLOYEE", "ADMIN"})
    void updateProductAdminAndEmployee() throws Exception {
        //@formatter:off
        this.mockMvc.perform(put(MANAGEMENT_ADD_PRODUCTS_PATH + "/{productId}", TEST_UUID).content(
                objectMapper.writeValueAsString(product))
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.name").value("iPhone"))
                .andExpect(jsonPath("$.picLocation").value("location"))
                .andExpect(jsonPath("$.price").value("$200"));
    }

    @Test
    @DisplayName("4- Test DELETE only Admin or Employee can DELETE products using the management path")
    @WithMockUser(username = "name", password = "pwd", roles = {"EMPLOYEE", "ADMIN"})
    void deleteProductAdminAndEmployee() throws Exception {
        this.mockMvc.perform(delete(MANAGEMENT_ADD_PRODUCTS_PATH + "/{productId}", TEST_UUID))
                .andExpect(status().isAccepted());
    }
}
