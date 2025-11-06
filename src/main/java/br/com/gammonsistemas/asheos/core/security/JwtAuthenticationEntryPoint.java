package br.com.gammonsistemas.asheos.core.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        // Define o status HTTP como 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // (Opcional) Adiciona o cabeçalho WWW-Authenticate, que é padrão para 401
        response.setHeader("WWW-Authenticate", "Bearer realm=\"asheos-api\"");

        // (Opcional) Retorna um JSON de erro mais amigável
        response.setContentType("application/json");
        response.getWriter().write(
                "{ \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Acesso negado. Por favor, forneça um token de autenticação válido.\"}");
    }
}
