package br.com.gammonsistemas.asheos.domain.occurrence;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.gammonsistemas.asheos.AbstractRestDocsTest;
import br.com.gammonsistemas.asheos.core.storage.StorageService;
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
        private AttachmentRepository attachmentRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @MockitoBean
        private StorageService storageService;

        private User mockLoggedUser;
        private Occurrence mockOccurrence;

        @BeforeEach
        void setUp() {
                occurrenceRepository.deleteAll();
                userRepository.deleteAll();

                // Cria o usuário no banco
                mockLoggedUser = UserMock.USER_JOHN_DOE();
                mockLoggedUser.setPassword(passwordEncoder.encode(UserMock.USER_PASSWORD));

                userRepository.save(mockLoggedUser);

                mockOccurrence = OccurrenceMock.OCCURRENCE_LAMPPOST();
                mockOccurrence.setReportedBy(mockLoggedUser);

                occurrenceRepository.save(mockOccurrence);
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
        @DisplayName("Deve falhar ao tentar criar uma ocorrência sem usuário autenticado")
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

        @Test
        @WithMockUser(username = "johndoe@test.com")
        @DisplayName("Deve listar todas as ocorrências")
        void testListOccurrences() throws Exception {
                // When & Then
                mockMvc.perform(get("/occurrences"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").exists())
                                .andExpect(jsonPath("$[0].title").value(OccurrenceMock.OCCURENCE_TITLE))
                                .andExpect(jsonPath("$[0].status").value("OPENED"))
                                .andDo(document("occurrences/list",
                                                responseFields(
                                                                fieldWithPath("[]").description(
                                                                                "Lista de ocorrências."),
                                                                fieldWithPath("[].id").description(
                                                                                "ID da ocorrência."),
                                                                fieldWithPath("[].title").description(
                                                                                "Título da ocorrência."),
                                                                fieldWithPath("[].description").description(
                                                                                "Descrição detalhada."),
                                                                fieldWithPath("[].status").description(
                                                                                "Status da ocorrência."),
                                                                fieldWithPath("[].reportedBy").description(
                                                                                "Objeto do usuário que reportou."),
                                                                fieldWithPath("[].reportedBy.id")
                                                                                .description("ID do usuário."),
                                                                fieldWithPath("[].reportedBy.name")
                                                                                .description("Nome do usuário."),
                                                                fieldWithPath("[].reportedBy.email")
                                                                                .description("Email do usuário."),
                                                                fieldWithPath("[].attachments")
                                                                                .description("Anexos da ocorrência."))));

        }

        @Test
        @WithMockUser(username = "johndoe@test.com")
        @DisplayName("Deve encontrar uma ocorrência por Id")
        void testFindOccurrenceById() throws Exception {
                // When & Then
                mockMvc.perform(get("/occurrences/{id}", mockOccurrence.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").exists())
                                .andExpect(jsonPath("$.title").value(OccurrenceMock.OCCURENCE_TITLE))
                                .andExpect(jsonPath("$.status").value("OPENED"))
                                .andDo(document("occurrences/get",
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
        @WithMockUser(username = "johndoe@test.com")
        @DisplayName("Deve atualizar o status de uma ocorrência")
        void testUpdateOccurrenceStatus() throws Exception {
                // Given
                String newStatus = OccurrenceStatus.IN_PROGRESS.toString();

                // When & Then
                mockMvc.perform(patch("/occurrences/{id}/status", mockOccurrence.getId())
                                .queryParam("status", newStatus)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(mockOccurrence.getId()))
                                .andExpect(jsonPath("$.status").value(newStatus))
                                .andDo(document("occurrences/update-status",
                                                queryParameters(parameterWithName("status").description(
                                                                "Novo status da ocorrência.")),
                                                responseFields(
                                                                fieldWithPath("id").description(
                                                                                "ID da ocorrência criada."),
                                                                fieldWithPath("title")
                                                                                .description("Título da ocorrência."),
                                                                fieldWithPath("description")
                                                                                .description("Descrição detalhada."),
                                                                fieldWithPath("status").description(
                                                                                "Status atualizado da ocorrência."),
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
        @WithMockUser(username = "johndoe@test.com")
        @DisplayName("Deve excluir uma ocorrência")
        void testDeleteOccurrence() throws Exception {
                // When
                mockMvc.perform(
                                delete("/occurrences/{id}", mockOccurrence.getId()))
                                .andExpect(status().isNoContent()) // Espera 204
                                .andDo(document("occurrences/delete"));
        }

        // Anexos

        @Test
        @WithMockUser(username = "johndoe@test.com")
        @DisplayName("Deve fazer o upload de um anexo")
        void testUploadAttachment() throws Exception {
                // Given
                MockMultipartFile file = new MockMultipartFile(
                                "file",
                                "test-upload.jpg",
                                MediaType.IMAGE_JPEG_VALUE,
                                "fake-image-bytes".getBytes());

                when(storageService.uploadFile(any(MultipartFile.class), anyString()))
                                .thenReturn("path/to/mock-file.jpg");
                // When & Then
                mockMvc.perform(
                                multipart("/occurrences/{id}/attachments", mockOccurrence.getId())
                                                .file(file))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.fileName").value("test-upload.jpg"))
                                .andExpect(jsonPath("$.filePath").isString())
                                .andDo(document("occurrences/attachments/upload",
                                                requestParts(
                                                                partWithName("file")
                                                                                .description("Arquivo a ser enviado.")),
                                                responseFields(
                                                                fieldWithPath("id").description("ID do anexo."),
                                                                fieldWithPath("fileName")
                                                                                .description("Nome do arquivo."),
                                                                fieldWithPath("filePath")
                                                                                .description("Caminho do arquivo."),
                                                                fieldWithPath("mimeType")
                                                                                .description("Tipo do arquivo."))));
        }

        @Test
        @WithMockUser(username = "stranger@test.com")
        @DisplayName("Deve falhar ao fazer o upload de um anexo quando usuário não é responsável")
        void testUploadAttachmentUnauthorized() throws Exception {
                // Given
                User stranger = new User();
                stranger.setEmail("stranger@test.com");
                stranger.setName("Stranger");
                stranger.setPassword("123");
                userRepository.save(stranger);

                MockMultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE,
                                "bytes".getBytes());
                // When & Then
                mockMvc.perform(
                                multipart("/occurrences/{id}/attachments", mockOccurrence.getId())
                                                .file(file))
                                // O serviço lança AccessDeniedException, o Spring trata como 403
                                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "johndoe@test.com")
        @DisplayName("Deve listar anexos de uma ocorrência")
        void testListAttachments() throws Exception {
                // Given
                Attachment attachment = new Attachment(
                                null,
                                "test-download.jpg",
                                "path/to/test-download.jpg",
                                "image/jpeg",
                                mockOccurrence);

                attachmentRepository.save(attachment);

                // When & Then
                mockMvc.perform(
                                get("/occurrences/{id}/attachments", mockOccurrence.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].fileName").value("test-download.jpg"))
                                .andExpect(jsonPath("$[0].filePath").isString())
                                .andDo(document("occurrences/attachments/list",
                                                responseFields(
                                                                fieldWithPath("[]").description("Lista de anexos."),
                                                                fieldWithPath("[].id").description("ID do anexo."),
                                                                fieldWithPath("[].fileName")
                                                                                .description("Nome do arquivo."),
                                                                fieldWithPath("[].filePath")
                                                                                .description("Caminho do arquivo."),
                                                                fieldWithPath("[].mimeType")
                                                                                .description("Tipo do arquivo."))));
        }

        @Test
        @WithMockUser(username = "johndoe@test.com")
        @DisplayName("Deve fazer o download de um anexo")
        void testDownloadAttachment() throws Exception {
                // Given
                MockMultipartFile file = new MockMultipartFile(
                                "file",
                                "test-download.jpg",
                                MediaType.IMAGE_JPEG_VALUE,
                                "fake-image-bytes".getBytes());

                Attachment attachment = new Attachment(
                                null,
                                "test-download.jpg",
                                "path/to/test-download.jpg",
                                MediaType.IMAGE_JPEG_VALUE,
                                mockOccurrence);

                when(storageService.downloadFile(anyString()))
                                .thenReturn(file.getBytes());

                attachmentRepository.save(attachment);

                // When & Then
                mockMvc.perform(
                                get("/occurrences/{id}/attachments/{attachmentId}/download", mockOccurrence.getId(),
                                                attachment.getId()))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE))
                                .andDo(document("occurrences/attachments/download",
                                                responseBody()));
        }

        @Test
        @WithMockUser(username = "johndoe@test.com")
        @DisplayName("Deve excluir um anexo")
        void testDeleteAttachment() throws Exception {
                // Given
                Attachment attachment = new Attachment();
                attachment.setOccurrence(mockOccurrence);
                attachment.setFileName("delete-me.txt");
                attachment.setFilePath("path/to/delete-me.txt");
                attachment.setMimeType("text/plain");
                attachmentRepository.save(attachment);

                doNothing().when(storageService).deleteFile("path/to/delete-me.txt");

                // When
                mockMvc.perform(
                                delete("/occurrences/{id}/attachments/{attachmentId}",
                                                mockOccurrence.getId(), attachment.getId()))
                                .andExpect(status().isNoContent()) // Espera 204
                                .andDo(document("occurrences/attachments/delete"));
                // Then
        }
}
