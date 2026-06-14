package com.itau.jwtvalidator.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integração da API de validação de JWT")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JwtValidationControllerIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtValidationControllerIntegrationTest.class);
    private static final String VALIDATION_ENDPOINT = "/api/v1/jwt/validate";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    @DisplayName("Caso 1: deve retornar valid=true para JWT válido com Name, Role e Seed primo")
    void shouldReturnTrueForOfficialCaseOne() throws Exception {
        LOGGER.info("Executing official test case 1: valid JWT must return valid=true");
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiNzg0MSIsIk5hbWUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05sIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg";

        mockMvc.perform(post(VALIDATION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    @Order(2)
    @DisplayName("Caso 2: deve retornar valid=false para JWT estruturalmente inválido")
    void shouldReturnFalseForOfficialCaseTwo() throws Exception {
        LOGGER.info("Executing official test case 2: invalid JWT must return valid=false");
        String token = "eyJhbGciOiJzI1NiJ9.dfsdfsfryJSr2xrIjoiQWRtaW4iLCJTZrkIjoiNzg0MSIsIk5hbrUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05fsdfsIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg";

        mockMvc.perform(post(VALIDATION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    @Order(3)
    @DisplayName("Caso 3: deve retornar valid=false quando a claim Name contém número")
    void shouldReturnFalseForOfficialCaseThree() throws Exception {
        LOGGER.info("Executing official test case 3: Name with numeric character must return valid=false");
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiRXh0ZXJuYWwiLCJTZWVkIjoiODgwMzciLCJOYW1lIjoiTTRyaWEgT2xpdmlhIn0.6YD73XWZYQSSMDf6H0i3-kylz1-TY_Yt6h1cV2Ku-Qs";

        mockMvc.perform(post(VALIDATION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    @Order(4)
    @DisplayName("Caso 4: deve retornar valid=false quando o JWT contém claim extra")
    void shouldReturnFalseForOfficialCaseFour() throws Exception {
        LOGGER.info("Executing official test case 4: extra claim must return valid=false");
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiTWVtYmVyIiwiT3JnIjoiQlIiLCJTZWVkIjoiMTQ2MjciLCJOYW1lIjoiVmFsZGlyIEFyYW5oYSJ9.cmrXV_Flm5mfdpfNUVopY_I2zeJUy4EZ4i3Fea98zvY";

        mockMvc.perform(post(VALIDATION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }

    private String requestBody(String token) {
        return """
                {
                  "token": "%s"
                }
                """.formatted(token);
    }
}
