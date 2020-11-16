package com.baghdadfocusit.webshop3d.controller.management;

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

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
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
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class CustomerManagementControllerTest {

  /**
   * Random UUID
   */
  public static final UUID TEST_UUID = UUID.randomUUID();
  /**
   * Test attributes
   */
  public static final String TEST_NAME = "Hayder";
  public static final String TEST_EMAIL = "test@email.com";
  public static final String TEST_PHONE = "009647901";
  public static final String TEST_ADDRESS = "BAGHDAD";
  public static final Long TEST_CUSTOMER_NR = 1234567890L;

  /**
   * Path to management endpoints
   */
  private static final String MANAGEMENT_ADD_CUSTOMERS_PATH = "/management/v1/customers";

  /**
   * Tested entity
   */
  private Customer customer;

  /**
   * Product service
   */
  @MockBean
  private CustomerService customerService;

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

  /**
   * Set up test class
   */
  @BeforeEach
  void setUp() {
    customer = Customer.builder()
                       .createdAt(LocalDate.now())
                       .id(TEST_UUID)
                       .name(TEST_NAME)
                       .email(TEST_EMAIL)
                       .phone(TEST_PHONE)
                       .address(TEST_ADDRESS)
                       .customerNumber(TEST_CUSTOMER_NR)
                       .build();
    when(customerService.getAllCustomers()).thenReturn(Collections.singletonList(customer));
    when(customerService.createNewCustomer(any(Customer.class))).thenReturn(customer);
    when(customerService.findCustomer(String.valueOf(TEST_UUID))).thenReturn(java.util.Optional.ofNullable(customer));
  }

  @Test
  @DisplayName("0- Test POST (create) management customer path does not exists")
  @WithMockUser(username = "name", password = "pwd", roles = {"EMPLOYEE", "ADMIN"})
  void postCustomers() throws Exception {
    this.mockMvc.perform(post(MANAGEMENT_ADD_CUSTOMERS_PATH).content(objectMapper.writeValueAsString(customer))
                                                           .contentType(MediaType.APPLICATION_JSON)
                                                           .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
  }

  @Test
  @DisplayName("1- Test CRUD management customer path is not accessible")
  void getAllProductsOGet() throws Exception {
    this.mockMvc.perform(get(MANAGEMENT_ADD_CUSTOMERS_PATH)).andDo(print()).andExpect(status().isForbidden());
    this.mockMvc.perform(delete(MANAGEMENT_ADD_CUSTOMERS_PATH + "/{customerId}", TEST_UUID))
                .andDo(print())
                .andExpect(status().isForbidden());
    this.mockMvc.perform(put(MANAGEMENT_ADD_CUSTOMERS_PATH + "/{customerId}",
                             TEST_UUID).content(objectMapper.writeValueAsString(customer))
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
  }


  @Test
  @DisplayName("2- Test CRUD Customer or anyone else does NOT have access to CRUD the management path")
  @WithMockUser(username = "name", password = "pwd", roles = {"CUSTOMER", "fhdgvuyeg"})
  void createProductCustomer() throws Exception {
    this.mockMvc.perform(get(MANAGEMENT_ADD_CUSTOMERS_PATH)).andDo(print()).andExpect(status().isForbidden());
    this.mockMvc.perform(delete(MANAGEMENT_ADD_CUSTOMERS_PATH + "/{customerId}", TEST_UUID))
                .andDo(print())
                .andExpect(status().isForbidden());
    this.mockMvc.perform(put(MANAGEMENT_ADD_CUSTOMERS_PATH + "/{customerId}",
                             TEST_UUID).content(objectMapper.writeValueAsString(customer))
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("3- Test GET management customer is accessible only for ADMIN and Employee")
  @WithMockUser(username = "name", password = "pwd", roles = {"EMPLOYEE", "ADMIN"})
  void getAllCustomers() throws Exception {
    this.mockMvc.perform(get(MANAGEMENT_ADD_CUSTOMERS_PATH))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().string(containsString(TEST_NAME)));
  }

  @Test
  @DisplayName("4- Test PUT management customer only Admin or Employee can CHANGE products using the management path")
  @WithMockUser(username = "name", password = "pwd", roles = {"EMPLOYEE", "ADMIN"})
  void updateCustomer() throws Exception {
    //@formatter:off
    this.mockMvc.perform(put(MANAGEMENT_ADD_CUSTOMERS_PATH + "/{customer}", TEST_UUID)
                             .content(objectMapper.writeValueAsString(customer))
                             .contentType(MediaType.APPLICATION_JSON)
                             .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.name").value(TEST_NAME))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.phone").value(TEST_PHONE))
                .andExpect(jsonPath("$.address").value(TEST_ADDRESS))
                .andExpect(jsonPath("$.customerNumber").value(TEST_CUSTOMER_NR));
  }

  @Test
  @DisplayName("5- Test DELETE only Admin or Employee can DELETE customers using the management path")
  @WithMockUser(username = "name", password = "pwd", roles = {"EMPLOYEE", "ADMIN"})
  void deleteCustomer() throws Exception {
    this.mockMvc.perform(delete(MANAGEMENT_ADD_CUSTOMERS_PATH + "/{customer}", TEST_UUID))
                .andExpect(status().isAccepted());
  }
}
