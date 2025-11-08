package br.com.gammonsistemas.asheos.domain.address;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
                                .andDo(document("addresses/create",
                                                requestFields(
                                                                fieldWithPath("street").description("Nome da rua."),
                                                                fieldWithPath("city").description("Nome da cidade."),
                                                                fieldWithPath("state").description(
                                                                                "Sigla do estado (2 caracteres)."),
                                                                fieldWithPath("zipCode")
                                                                                .description("CEP (Código Postal).")),
                                                responseFields(
                                                                fieldWithPath("id")
                                                                                .description("ID do endereço criado."),
                                                                fieldWithPath("street").description("Nome da rua."),
                                                                fieldWithPath("city").description("Nome da cidade."),
                                                                fieldWithPath("state").description("Sigla do estado."),
                                                                fieldWithPath("zipCode").description("CEP."))));
        }

        @Test
        @WithMockUser(username = "johndoe@test.com")
        @DisplayName("Deve listar os endereços do usuário logado")
        void testGetAddressesByUser() throws Exception {
                // Given
                Address address = AddressMock.ADDRESS_JOHN_DOE();
                address.setUser(mockLoggedUser);
                addressRepository.save(address);

                // When & Then
                mockMvc.perform(get("/addresses"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").exists())
                                .andExpect(jsonPath("$[0].street").value(AddressMock.ADDRESS_STREET))
                                .andDo(document("addresses/list",
                                                responseFields(
                                                                fieldWithPath("[]").description(
                                                                                "Lista de endereços."),
                                                                fieldWithPath("[].id")
                                                                                .description("ID do endereço criado."),
                                                                fieldWithPath("[].street").description("Nome da rua."),
                                                                fieldWithPath("[].city").description("Nome da cidade."),
                                                                fieldWithPath("[].state")
                                                                                .description("Sigla do estado."),
                                                                fieldWithPath("[].zipCode").description("CEP."))));
        }

        @Test
        @WithMockUser(username = "johndoe@test.com")
        @DisplayName("Deve atualizar um endereço")
        void testUpdateAddress() throws Exception {
                // Given
                Address address = AddressMock.ADDRESS_JOHN_DOE();
                address.setUser(mockLoggedUser);
                addressRepository.save(address);

                AddressRequest request = new AddressRequest(
                                "Rua 09",
                                AddressMock.ADDRESS_CITY,
                                AddressMock.ADDRESS_STATE,
                                AddressMock.ADDRESS_ZIP_CODE);
                String jsonRequest = objectMapper.writeValueAsString(request);

                // When & Then
                mockMvc.perform(put("/addresses/{id}", address.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").exists())
                                .andExpect(jsonPath("$.street").value("Rua 09"))
                                .andDo(document("addresses/update",
                                                requestFields(
                                                                fieldWithPath("street").description("Nome da rua."),
                                                                fieldWithPath("city").description("Nome da cidade."),
                                                                fieldWithPath("state").description(
                                                                                "Sigla do estado (2 caracteres)."),
                                                                fieldWithPath("zipCode")
                                                                                .description("CEP (Código Postal).")),
                                                responseFields(
                                                                fieldWithPath("id")
                                                                                .description("ID do endereço criado."),
                                                                fieldWithPath("street").description("Nome da rua."),
                                                                fieldWithPath("city").description("Nome da cidade."),
                                                                fieldWithPath("state").description("Sigla do estado."),
                                                                fieldWithPath("zipCode").description("CEP."))));
        }

        @Test
        @WithMockUser(username = "johndoe@test.com")
        @DisplayName("Deve excluir um endereço")
        void testDeleteAddress() throws Exception {
                // Given
                Address address = AddressMock.ADDRESS_JOHN_DOE();
                address.setUser(mockLoggedUser);
                addressRepository.save(address);

                // When & Then
                mockMvc.perform(
                                delete("/addresses/{id}", address.getId()))
                                .andExpect(status().isNoContent()) // Espera 204
                                .andDo(document("addresses/delete"));
        }
}
