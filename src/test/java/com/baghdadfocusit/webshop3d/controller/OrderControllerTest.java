package com.baghdadfocusit.webshop3d.controller;

import com.baghdadfocusit.webshop3d.entities.Order;
import com.baghdadfocusit.webshop3d.model.order.OrderProductsResponse;
import com.baghdadfocusit.webshop3d.model.order.OrderRequestJson;
import com.baghdadfocusit.webshop3d.model.order.OrderResponseJson;
import com.baghdadfocusit.webshop3d.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderControllerTest {

    /**
     * Path to management endpoints
     */
    private static final String ADD_ORDER_PATH = "/resources/orders/add-order";

    /**
     * Tested class
     */
    private OrderResponseJson orderResponseJson;

    /**
     * Product service mock
     */
    @MockBean
    private OrderService orderService;

    /**
     * Product service mock
     */
    @Mock
    private OrderRequestJson orderRequestJson;

    /**
     * Mock MVC
     */
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        orderResponseJson = new OrderResponseJson(UUID.randomUUID(), LocalDate.now(), "Amsterdam", "somename", "3D-123232", 1200,
                                                  Order.OrderState.RECEIVED, null, "alkaradh", "street 52", "1212121", "email",
                                                  "notes",null, 1,
                                                  List.of(new OrderProductsResponse("iPhone 10", 1200, 1, 1)));
        when(orderService.creatOrder(any(OrderRequestJson.class))).thenReturn(orderResponseJson);
    }

    @Test
    @DisplayName("0- Create one order")
    void createOneOrder() throws Exception {
        this.mockMvc.perform(post(ADD_ORDER_PATH))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().string(containsString("iPhone")));
    }

    //    @Test
    //    @DisplayName("1- Test POST NO one can post to products")
    //    void postProductsNotOkPost() throws Exception {
    //        this.mockMvc.perform(post(ADD_PRODUCTS_PATH)).andDo(print()).andExpect(status().isMethodNotAllowed());
    //    }
    //
    //
    //    @Test
    //    @DisplayName("2- Test DELETE NO one can delete from products")
    //    void postProductsNotOkDelete() throws Exception {
    //        this.mockMvc.perform(delete(ADD_PRODUCTS_PATH)).andDo(print()).andExpect(status().isMethodNotAllowed());
    //    }
    //
    //
    //    @Test
    //    @DisplayName("3- Test PUT NO can change products details")
    //    void postProductsNotOkPut() throws Exception {
    //        this.mockMvc.perform(put(ADD_PRODUCTS_PATH)).andDo(print()).andExpect(status().isMethodNotAllowed());
    //    }
}
