package br.com.gammonsistemas.asheos.domain.auth.dto;

import lombok.Builder;

@Builder
public record TokenResponse(
        String token,
        String type) {
    public TokenResponse {
        type = "Bearer";
    }
}