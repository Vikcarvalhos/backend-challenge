package com.itau.jwtvalidator.config;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Documentação OpenAPI")
class OpenApiDocumentationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Deve expor a especificação OpenAPI com o endpoint de validação")
    void shouldExposeOpenApiSpecification() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("JWT Validator API"))
                .andExpect(jsonPath("$.info.description", containsString("Regras de validacao")))
                .andExpect(jsonPath("$.info.description", containsString("Name, Role e Seed")))
                .andExpect(jsonPath("$.info.description", containsString("validacao criptografica da assinatura")))
                .andExpect(jsonPath("$.paths['/api/v1/jwt/validate']").exists());
    }

    @Test
    @DisplayName("Deve documentar os exemplos oficiais do desafio")
    void shouldDocumentOfficialChallengeExamples() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/v1/jwt/validate'].post.description", containsString("valid=false")))
                .andExpect(jsonPath("$.paths['/api/v1/jwt/validate'].post.requestBody.content['application/json'].examples['Caso 1 - valido']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/jwt/validate'].post.requestBody.content['application/json'].examples['Caso 2 - JWT invalido']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/jwt/validate'].post.requestBody.content['application/json'].examples['Caso 3 - Name com numero']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/jwt/validate'].post.requestBody.content['application/json'].examples['Caso 4 - Claim extra']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/jwt/validate'].post.responses['200'].content['application/json'].examples['JWT valido']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/jwt/validate'].post.responses['200'].content['application/json'].examples['JWT invalido']").exists());
    }

    @Test
    @DisplayName("Deve expor o Swagger UI")
    void shouldExposeSwaggerUi() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}
