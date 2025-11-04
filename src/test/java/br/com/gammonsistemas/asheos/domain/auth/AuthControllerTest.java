package br.com.gammonsistemas.asheos.domain.auth;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.gammonsistemas.asheos.AbstractRestDocsTest;
import br.com.gammonsistemas.asheos.domain.auth.dto.LoginRequest;
import br.com.gammonsistemas.asheos.domain.auth.dto.RegisterRequest;
import br.com.gammonsistemas.asheos.domain.user.UserRepository;

public class AuthControllerTest extends AbstractRestDocsTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve registrar um novo usuário com sucesso")
    void testRegisterUserSuccess() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest("New User", "newuser@test.com", "password123");

        // When & Then
        // (1. Checa a resposta HTTP, 2. Checa o conteúdo do banco, 3. Gera a
        // documentação)
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andDo(document("auth/register",
                        requestFields(
                                fieldWithPath("name")
                                        .description("Nome completo do usuário."),
                                fieldWithPath("email")
                                        .description("E-mail único do usuário."),
                                fieldWithPath("password")
                                        .description("Senha (mínimo 6 caracteres).")),
                        responseFields(
                                fieldWithPath("token")
                                        .description("Token de autenticação JWT."),
                                fieldWithPath("type")
                                        .description("Tipo de token (Bearer)."))));

        assertTrue(userRepository.findByEmail("newuser@test.com").isPresent());
    }

    @Test
    @DisplayName("Deve logar com as credenciais e retornar token")
    void testLoginUserSuccess() throws Exception {
        // Given
        authService.register(new RegisterRequest("Login User", "loginuser@test.com", "password123"));

        LoginRequest request = new LoginRequest("loginuser@test.com", "password123");
        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andDo(document("auth/login",
                        requestFields(
                                fieldWithPath("email")
                                        .description("E-mail do usuário."),
                                fieldWithPath("password")
                                        .description("Senha do usuário.")),
                        responseFields(
                                fieldWithPath("token")
                                        .description("Token de autenticação JWT."),
                                fieldWithPath("type")
                                        .description("Tipo de token (Bearer)."))));
    }

    @Test
    @DisplayName("Deve falhar ao logar com credenciais inválidas")
    void testLoginUserFailure() throws Exception {
        // Given
        LoginRequest request = new LoginRequest("wronguser@test.com", "wrongPassword123");

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
