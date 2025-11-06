package br.com.gammonsistemas.asheos.domain.occurrence;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.gammonsistemas.asheos.AbstractRestDocsTest;
import br.com.gammonsistemas.asheos.domain.occurrence.dto.OccurrenceRequest;
import br.com.gammonsistemas.asheos.domain.user.User;
import br.com.gammonsistemas.asheos.domain.user.UserMock;
import br.com.gammonsistemas.asheos.domain.user.UserRepository;

public class OccurrenceControllerTest extends AbstractRestDocsTest {

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private OccurrenceRepository occurrenceRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        private User mockLoggedUser;

        @BeforeEach
        void setUp() {
                occurrenceRepository.deleteAll();
                userRepository.deleteAll();

                // Cria o usuário no banco
                mockLoggedUser = UserMock.USER_JOHN_DOE();
                mockLoggedUser.setPassword(passwordEncoder.encode(UserMock.USER_PASSWORD));

                userRepository.save(mockLoggedUser);
        }

        @Test
        @WithMockUser(username = "johndoe@test.com")
        @DisplayName("Deve criar uma ocorrência")
        void testCreateOccurrence() throws Exception {
                // Given
                OccurrenceRequest request = new OccurrenceRequest(
                                OccurrenceMock.OCCURENCE_TITLE,
                                OccurrenceMock.OCCURRENCE_DESCRIPTION);
                String requestJson = objectMapper.writeValueAsString(request);

                // When & Then
                mockMvc.perform(post("/occurrences")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                                .andExpect(status().isCreated()) // Espera 201 Created
                                .andExpect(jsonPath("$.id").exists())
                                .andExpect(jsonPath("$.title").value(OccurrenceMock.OCCURENCE_TITLE))
                                .andExpect(jsonPath("$.status").value("OPENED"))
                                .andExpect(jsonPath("$.reportedBy.email").value("johndoe@test.com"))
                                .andExpect(jsonPath("$.reportedBy.password").doesNotExist())
                                .andDo(document("occurrences/create",
                                                requestFields(
                                                                fieldWithPath("title").description(
                                                                                "Título breve da ocorrência."),
                                                                fieldWithPath("description").description(
                                                                                "Descrição detalhada do problema.")),
                                                responseFields(
                                                                fieldWithPath("id").description(
                                                                                "ID da ocorrência criada."),
                                                                fieldWithPath("title")
                                                                                .description("Título da ocorrência."),
                                                                fieldWithPath("description")
                                                                                .description("Descrição detalhada."),
                                                                fieldWithPath("status").description(
                                                                                "Status inicial (ex: OPENED)."),
                                                                fieldWithPath("reportedBy").description(
                                                                                "Objeto do usuário que reportou."),
                                                                fieldWithPath("reportedBy.id")
                                                                                .description("ID do usuário."),
                                                                fieldWithPath("reportedBy.name")
                                                                                .description("Nome do usuário."),
                                                                fieldWithPath("reportedBy.email")
                                                                                .description("Email do usuário."),
                                                                fieldWithPath("attachments")
                                                                                .description("Anexos da ocorrência."))));
        }

        @Test
        @DisplayName("Deve falhar ao tentar acessar rota sem usuário autenticado")
        void testCreateOccurrenceWithoutAuthentication() throws Exception {
                // Given
                OccurrenceRequest request = new OccurrenceRequest(
                                OccurrenceMock.OCCURENCE_TITLE,
                                OccurrenceMock.OCCURRENCE_DESCRIPTION);

                // When & Then
                mockMvc.perform(post("/occurrences")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isUnauthorized());
        }
}
