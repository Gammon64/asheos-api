package br.com.gammonsistemas.asheos.domain.user;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import br.com.gammonsistemas.asheos.AbstractRestDocsTest;

public class UserControllerTest extends AbstractRestDocsTest {

    @Test
    @WithMockUser(username = "johndoe@test.com")
    @DisplayName("Deve retornar o usuário autenticado")
    void testGetAuthUser() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("johndoe@test.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andDo(document("users/me",
                        responseFields(
                                fieldWithPath("id").description("ID do usuário."),
                                fieldWithPath("name").description("Nome do usuário."),
                                fieldWithPath("email").description("Email do usuário."))));
    }

}
