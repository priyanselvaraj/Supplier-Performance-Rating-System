package com.supplier.sprsystem.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplier.sprsystem.dto.request.SupplierRequest;
import com.supplier.sprsystem.model.entity.SupplierStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class SupplierControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("IT-06: ADMIN can fetch paginated suppliers list")
    void testGetSuppliersAsAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/suppliers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", notNullValue()));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("IT-07: MANAGER can fetch paginated suppliers list")
    void testGetSuppliersAsManager() throws Exception {
        mockMvc.perform(get("/api/v1/suppliers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @DisplayName("IT-08: Unauthenticated access to /api/v1/suppliers returns 401")
    void testGetSuppliersUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/suppliers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("IT-09: ADMIN can create a new supplier")
    void testCreateSupplierAsAdmin() throws Exception {
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        SupplierRequest request = SupplierRequest.builder()
                .name("Integration Supplier " + uniqueSuffix)
                .email("supplier_" + uniqueSuffix + "@vendor.com")
                .contactPerson("Jane Doe")
                .phone("1234567890")
                .city("San Francisco")
                .country("USA")
                .categoryId(1L)
                .status(SupplierStatus.ACTIVE)
                .build();

        mockMvc.perform(post("/api/v1/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.supplierCode", startsWith("SUP-")));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("IT-10: MANAGER can create a supplier")
    void testCreateSupplierAsManager() throws Exception {
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        SupplierRequest request = SupplierRequest.builder()
                .name("Manager Supplier " + uniqueSuffix)
                .email("manager_" + uniqueSuffix + "@vendor.com")
                .contactPerson("Bob Manager")
                .phone("9876543210")
                .city("Chicago")
                .country("USA")
                .categoryId(1L)
                .status(SupplierStatus.ACTIVE)
                .build();

        mockMvc.perform(post("/api/v1/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.supplierCode", startsWith("SUP-")));
    }
}
