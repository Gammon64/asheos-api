package br.com.gammonsistemas.asheos.domain.address;

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
import br.com.gammonsistemas.asheos.domain.address.dto.AddressRequest;
import br.com.gammonsistemas.asheos.domain.user.User;
import br.com.gammonsistemas.asheos.domain.user.UserMock;
import br.com.gammonsistemas.asheos.domain.user.UserRepository;

public class AddressControllerTest extends AbstractRestDocsTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private User mockLoggedUser;

    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
        userRepository.deleteAll();

        // Cria o usuário no banco
        mockLoggedUser = UserMock.USER_JOHN_DOE();
        mockLoggedUser.setPassword(passwordEncoder.encode(UserMock.USER_PASSWORD));

        userRepository.save(mockLoggedUser);
    }

    @Test
    @WithMockUser(username = "johndoe@test.com")
    @DisplayName("Deve criar um endereço")
    void testCreateAddress() throws Exception {
        // Given
        AddressRequest request = new AddressRequest(
                AddressMock.ADDRESS_STREET,
                AddressMock.ADDRESS_CITY,
                AddressMock.ADDRESS_STATE,
                AddressMock.ADDRESS_ZIP_CODE);
        String jsonRequest = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(post("/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.street").value(AddressMock.ADDRESS_STREET))
                .andExpect(jsonPath("$.user.id").value(mockLoggedUser.getId()))
                .andExpect(jsonPath("$.user.password").doesNotExist())
                .andDo(document("addresses/create",
                        requestFields(
                                fieldWithPath("street").description("Nome da rua."),
                                fieldWithPath("city").description("Nome da cidade."),
                                fieldWithPath("state").description("Sigla do estado (2 caracteres)."),
                                fieldWithPath("zipCode").description("CEP (Código Postal).")),
                        responseFields(
                                fieldWithPath("id").description("ID do endereço criado."),
                                fieldWithPath("street").description("Nome da rua."),
                                fieldWithPath("city").description("Nome da cidade."),
                                fieldWithPath("state").description("Sigla do estado."),
                                fieldWithPath("zipCode").description("CEP."),
                                fieldWithPath("user")
                                        .description("Objeto do usuário associado (omitindo dados sensíveis)."),
                                fieldWithPath("user.id").description("ID do usuário."),
                                fieldWithPath("user.name").description("Nome do usuário."),
                                fieldWithPath("user.email").description("Email do usuário."))));
    }

}
