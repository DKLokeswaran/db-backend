package com.lokeswarandk.db_backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lokeswarandk.db_backend.dto.request.UpsertUserRequest;
import com.lokeswarandk.db_backend.dto.response.MobilePrefixSearchResponse;
import com.lokeswarandk.db_backend.dto.response.UserResponse;
import com.lokeswarandk.db_backend.exception.GlobalExceptionHandler;
import com.lokeswarandk.db_backend.exception.ResourceNotFoundException;
import com.lokeswarandk.db_backend.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTests {

    private static final String VALID_USER_JSON =
            """
            {
              "name": "kandasamy",
              "mobileNo": "9994722907",
              "addressLine": "tsr layout",
              "locality": "tiruppur",
              "state": "tn",
              "country": "india",
              "pincode": "641607"
            }
            """;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private UserService userService;

    @Test
    void addUser_returns201WithUserResponse() throws Exception {
        when(userService.create(any(UpsertUserRequest.class))).thenReturn(sampleUserResponse());

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(VALID_USER_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("kandasamy"))
                .andExpect(jsonPath("$.mobileNo").value("9994722907"));
    }

    @Test
    void addUser_returns400WhenValidationFails() throws Exception {
        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.details.name").value("User name is required"));
    }

    @Test
    void getUser_returns200WithUserResponse() throws Exception {
        when(userService.findById(1L)).thenReturn(sampleUserResponse());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("kandasamy"));
    }

    @Test
    void getUser_returns404WhenNotFound() throws Exception {
        when(userService.findById(99L))
                .thenThrow(ResourceNotFoundException.forResourceWithId("User", 99L));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"))
                .andExpect(jsonPath("$.message").value("No user with id 99 exists"));
    }

    @Test
    void listUsers_returnsAllUsers() throws Exception {
        when(userService.findAll()).thenReturn(List.of(sampleUserResponse()));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("kandasamy"));
    }

    @Test
    void listUsers_filtersByMobileWhenProvided() throws Exception {
        when(userService.findByMobileNo("9994722907")).thenReturn(List.of(sampleUserResponse()));

        mockMvc.perform(get("/api/users").param("mobile", "9994722907"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].mobileNo").value("9994722907"));

        verify(userService).findByMobileNo("9994722907");
    }

    @Test
    void updateUser_returns200WithUserResponse() throws Exception {
        when(userService.update(eq(1L), any(UpsertUserRequest.class)))
                .thenReturn(sampleUserResponse());

        mockMvc.perform(
                        put("/api/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(VALID_USER_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteUser_returns200WithMessagePayload() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"))
                .andExpect(jsonPath("$.id").value(1));

        verify(userService).deleteById(1L);
    }

    @Test
    void searchMobileByPrefix_returnsMobilePrefixSearchResponse() throws Exception {
        when(userService.searchMobileNosByPrefix("99", null))
                .thenReturn(new MobilePrefixSearchResponse(List.of("9994722907")));

        mockMvc.perform(get("/api/users/search/mobile").param("prefix", "99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mobileNos[0]").value("9994722907"));
    }

    @Test
    void searchMobileByPrefix_returns400WhenPrefixInvalid() throws Exception {
        when(userService.searchMobileNosByPrefix("9", null))
                .thenThrow(new IllegalArgumentException("prefix must be at least 2 characters"));

        mockMvc.perform(get("/api/users/search/mobile").param("prefix", "9"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad request"))
                .andExpect(jsonPath("$.message").value("prefix must be at least 2 characters"));
    }

    private static UserResponse sampleUserResponse() {
        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setName("kandasamy");
        response.setMobileNo("9994722907");
        response.setAddressLine("tsr layout");
        response.setLocality("tiruppur");
        response.setState("tn");
        response.setCountry("india");
        response.setPincode("641607");
        response.setCreatedAt(LocalDateTime.parse("2026-05-31T12:00:00"));
        return response;
    }
}
