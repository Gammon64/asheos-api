package br.com.gammonsistemas.asheos.core.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Busca o token no cabeçalho da requisição
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Verifica se existe o token e se começa com Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // Extrai o token
        jwt = authHeader.substring(7);

        // Busca o email no token
        try {
            userEmail = tokenService.extractUsername(jwt);
        } catch (AccessDeniedException e) {
            // Se for inválido, segue a cadeia de filtros
            filterChain.doFilter(request, response);
            return;
        }

        // Se o email for válido e não houver autenticação no contexto
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Carrega o usuário
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if (tokenService.isTokenValid(jwt, userDetails)) {
                // Cria a autenticação
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                        null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Usuário autenticado
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        }

        // Avança o filtro
        filterChain.doFilter(request, response);
    }
}
