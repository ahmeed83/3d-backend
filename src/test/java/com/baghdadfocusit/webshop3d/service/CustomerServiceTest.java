package com.baghdadfocusit.webshop3d.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class CustomerServiceTest {

  /**
   * Random UUID.
   */
  private final UUID randomUUID = UUID.randomUUID();

  /**
   * Mocked repository
   */
  @Mock
  CustomerRepository customerRepository;

  /**
   * Mocked service
   */
  @InjectMocks
  CustomerService customerService;

  /**
   * Tested entity
   */
  private Customer customer;

  @BeforeEach
  void setUp() {
    customer = Customer.builder()
                       .id(randomUUID)
                       .name("Hayder")
                       .customerNumber(100436764L)
                       .address("baghdad")
                       .phone("0790111111")
                       .email("email@mail.com")
                       .build();
    when(customerRepository.save(any(Customer.class))).thenReturn(customer);
    when(customerRepository.findAll()).thenReturn(Collections.singletonList(customer));
    when(customerRepository.findById(randomUUID)).thenReturn(java.util.Optional.ofNullable(customer));
  }

  @Test
  void createNewCustomer() {
    final var newCustomer = customerService.createNewCustomer(customer);
    assertNotNull(newCustomer);
    assertNull(newCustomer.getUpdatedAt());
    assertEquals("Hayder", newCustomer.getName());
    assertEquals(LocalDate.now(), newCustomer.getCreatedAt());
  }

  @Test
  void getAllCustomers() {
    final var newCustomers = (List<Customer>) customerService.getAllCustomers();
    assertNotNull(newCustomers);
    assertEquals(1, newCustomers.size());
    assertNull(newCustomers.get(0).getUpdatedAt());
    assertEquals("Hayder", newCustomers.get(0).getName());
  }

  @Test
  void findCustomer() {
    final var foundCustomer = customerService.findCustomer(String.valueOf(randomUUID));
    assertNotNull(foundCustomer);
    assertEquals("Hayder", foundCustomer.get().getName());
  }

  @Test
  void deleteCustomer() {
    customerService.deleteCustomer(String.valueOf(randomUUID));
    verify(customerRepository, times(1)).deleteById(eq(randomUUID));
  }
}
