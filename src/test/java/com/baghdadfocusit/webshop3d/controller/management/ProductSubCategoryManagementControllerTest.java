package com.baghdadfocusit.webshop3d.controller.management;

import com.baghdadfocusit.webshop3d.entities.Category;
import com.baghdadfocusit.webshop3d.model.SubCategoryJsonResponse;
import com.baghdadfocusit.webshop3d.repository.CategoryRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class ProductSubCategoryManagementControllerTest {

    public static final UUID CATEGORY_ID = UUID.randomUUID();

    private SubCategoryJsonResponse subCategoryJson;

    private Category category;

    private static final String MANAGEMENT_V_1_SUB_CATEGORY_PATH = "/management/v1/sub-category";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryRepository categoryRepositoryMock;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(CATEGORY_ID).name("Laptop").createdAt(LocalDate.now()).build();
        subCategoryJson = new SubCategoryJsonResponse(UUID.randomUUID(), "MacBook", String.valueOf(CATEGORY_ID));
    }

    @Test
    @DisplayName("0- Test GET management sub category path is not accessible")
    void getAllSubCategoryOGet() throws Exception {
        this.mockMvc.perform(get(MANAGEMENT_V_1_SUB_CATEGORY_PATH)).andDo(print()).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("1- Test GET management category from Admin or anyone else does have access to the " + "management " + "path")
    @WithMockUser(username = "name", password = "pwd", roles = {"EMPLOYEE", "ADMIN"})
    void getAllSubCategoryGetAdmin() throws Exception {
        this.mockMvc.perform(
                get(MANAGEMENT_V_1_SUB_CATEGORY_PATH + "/{categoryId}", String.valueOf(CATEGORY_ID)).content(
                        objectMapper.writeValueAsString(subCategoryJson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isAccepted());
    }

    @Test
    @DisplayName("2- Test GET management category from Customer or anyone else does NOT have access to the " +
            "management path")
    @WithMockUser(username = "name", password = "pwd", roles = {"CUSTOMER", "fhdgvuyeg"})
    void createSubCategoryNotAdmin() throws Exception {
        this.mockMvc.perform(post(MANAGEMENT_V_1_SUB_CATEGORY_PATH + "/add-sub-category").content(
                objectMapper.writeValueAsString(subCategoryJson))
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("3- Test POST only Admin or Employee can ADD Sub Category using the management path")
    @WithMockUser(username = "name", password = "pwd", roles = {"EMPLOYEE", "ADMIN"})
    void createSubCategoryAdminAndEmployee() throws Exception {
        when(categoryRepositoryMock.findById(CATEGORY_ID)).thenReturn(Optional.of(category));

        this.mockMvc.perform(post(MANAGEMENT_V_1_SUB_CATEGORY_PATH + "/add-sub-category").content(
                objectMapper.writeValueAsString(subCategoryJson))
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }
}
