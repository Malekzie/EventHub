package com.eventhub.eventhub_api.security;

import com.eventhub.eventhub_api.dto.LoginDTO;
import com.eventhub.eventhub_api.dto.PasswordResetDTO;
import com.eventhub.eventhub_api.dto.PasswordResetRequestDTO;
import com.eventhub.eventhub_api.model.Role;
import com.eventhub.eventhub_api.model.User;
import com.eventhub.eventhub_api.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();

        User user = new User();
        user.setEmail("user@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        User admin = new User();
        admin.setEmail("admin@test.com");
        admin.setFirstName("Test");
        admin.setLastName("Admin");
        admin.setPassword(passwordEncoder.encode("password123"));
        admin.setRole(Role.ADMIN);
        admin.setCreatedAt(LocalDateTime.now());
        userRepository.save(admin);

        userToken = obtainToken("user@test.com", "password123");
        adminToken = obtainToken("admin@test.com", "password123");
    }

    private String obtainToken(String email, String password) throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setEmail(email);
        dto.setPassword(password);

        String response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void testPublicEventAccess() throws Exception {
        mockMvc.perform(get("/api/v1/events"))
                .andExpect(status().isOk());
    }

    @Test
    void testLoginReturnsJwt() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("user@test.com");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("user@test.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void testInvalidLoginReturns401() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("user@test.com");
        dto.setPassword("wrongpassword");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUserCanCreateRegistration() throws Exception {
        String body = "{\"items\": [{\"eventId\": 999, \"quantity\": 1}]}";

        mockMvc.perform(post("/api/v1/registrations")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertThat(status)
                            .as("USER should pass security (not get 401/403); got " + status)
                            .isNotIn(401, 403);
                });
    }

    @Test
    void testUnauthenticatedCannotCreateRegistration() throws Exception {
        String body = "{\"items\": [{\"eventId\": 1, \"quantity\": 1}]}";

        mockMvc.perform(post("/api/v1/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAdminCanCreateEvent() throws Exception {
        String body = """
                {
                  "name": "Test Event",
                  "description": "A test event",
                  "location": "Calgary",
                  "startDate": "2026-06-01T10:00:00",
                  "endDate": "2026-06-01T18:00:00",
                  "ticketPrice": 25.00,
                  "totalTickets": 100,
                  "categoryId": 999
                }
                """;

        mockMvc.perform(post("/api/v1/events")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertThat(status)
                            .as("ADMIN should pass security (not get 401/403); got " + status)
                            .isNotIn(401, 403);
                });
    }

    @Test
    void testUserCannotCreateEvent() throws Exception {
        String body = """
                {
                  "name": "Test Event",
                  "description": "A test event",
                  "location": "Calgary",
                  "startDate": "2026-06-01T10:00:00",
                  "endDate": "2026-06-01T18:00:00",
                  "ticketPrice": 25.00,
                  "totalTickets": 100,
                  "categoryId": 1
                }
                """;

        mockMvc.perform(post("/api/v1/events")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAdminCanViewAllRegistrations() throws Exception {
        mockMvc.perform(get("/api/v1/registrations")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testUserCannotViewAllRegistrations() throws Exception {
        mockMvc.perform(get("/api/v1/registrations")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testPasswordResetFlow() throws Exception {
        PasswordResetRequestDTO requestDTO = new PasswordResetRequestDTO();
        requestDTO.setEmail("user@test.com");

        String resetResponse = mockMvc.perform(post("/api/v1/auth/password-reset-request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resetToken").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        String resetToken = objectMapper.readTree(resetResponse).get("resetToken").asText();

        PasswordResetDTO resetDTO = new PasswordResetDTO();
        resetDTO.setToken(resetToken);
        resetDTO.setNewPassword("newpassword123");

        mockMvc.perform(post("/api/v1/auth/password-reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isNotEmpty());

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("user@test.com");
        loginDTO.setPassword("newpassword123");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
}
