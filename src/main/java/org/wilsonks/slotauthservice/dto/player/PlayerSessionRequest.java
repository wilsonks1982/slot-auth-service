package org.wilsonks.slotauthservice.dto.player;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlayerSessionRequest(
        @NotBlank @Size(min = 1, max = 8)
        String uid,
        @NotBlank @Size(min = 4, max = 4)
        String pin
) {
}
