package br.com.gammonsistemas.asheos.core.security;

import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class TokenService {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private final String ISSUER = "asheos-api"; // Emissor do token

    /**
     * Gera um novo token JWT para o usuário
     */
    public String generateToken(UserDetails userDetails) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        Instant now = Instant.now();
        Instant expirationTime = now.plusMillis(expirationMs);

        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(userDetails.getUsername())
                .withIssuedAt(now)
                .withExpiresAt(expirationTime)
                .sign(algorithm);
    }

    /**
     * Extrai o email do token JWT
     * 
     * @param jwt
     * @return
     */
    public String extractUsername(String jwt) {
        DecodedJWT decodedJWT = verifyToken(jwt);
        return decodedJWT.getSubject();
    }

    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        try {
            DecodedJWT decodedJWT = verifyToken(jwt);
            String username = decodedJWT.getSubject();

            // Verifica se o subject é o mesmo e se o token não expirou
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(decodedJWT));
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    private DecodedJWT verifyToken(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();

        return verifier.verify(token);
    }

    private boolean isTokenExpired(DecodedJWT decodedJWT) {
        return decodedJWT.getExpiresAt().before(new Date());
    }
}
