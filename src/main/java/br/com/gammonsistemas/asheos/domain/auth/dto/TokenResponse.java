package br.com.gammonsistemas.asheos.domain.auth.dto;

public record TokenResponse(
        String token,
        String type) {
    public TokenResponse {
        type = "Bearer";
    }
}