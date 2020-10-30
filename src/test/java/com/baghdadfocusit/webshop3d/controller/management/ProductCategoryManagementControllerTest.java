package com.baghdadfocusit.webshop3d.controller.management;

import com.baghdadfocusit.webshop3d.model.CategoryJsonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class ProductCategoryManagementControllerTest {

    private CategoryJsonResponse categoryJson;

    private static final String MANAGEMENT_V_1_CATEGORY_PATH = "/management/v1/category";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        categoryJson = new CategoryJsonResponse("uuid", "hello", "Laptop");
    }

    @Test
    @DisplayName("0- Test GET management category path is not accessible")
    void getAllCategoriesGet() throws Exception {
        this.mockMvc.perform(get(MANAGEMENT_V_1_CATEGORY_PATH)).andDo(print()).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("1- Test GET management category from Customer or anyone else does NOT have access to the " +
            "management path")
    @WithMockUser(username = "name", password = "pwd", roles = {"CUSTOMER", "fhdgvuyeg"})
    void createCategoryNotAdmin() throws Exception {
        this.mockMvc.perform(post(MANAGEMENT_V_1_CATEGORY_PATH + "/add-category").content(
                objectMapper.writeValueAsString(categoryJson))
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("2- Test POST only Admin or Employee can ADD Category using the management path")
    @WithMockUser(username = "name", password = "pwd", roles = {"EMPLOYEE", "ADMIN"})
    void createCategoryAdminAndEmployee() throws Exception {
        this.mockMvc.perform(post(MANAGEMENT_V_1_CATEGORY_PATH + "/add-category").content(
                objectMapper.writeValueAsString(categoryJson))
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }
}
