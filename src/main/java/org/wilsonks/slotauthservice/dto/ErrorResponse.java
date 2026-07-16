package org.wilsonks.slotauthservice.dto;

import java.time.Instant;

public record ErrorResponse(
        Integer status,
        Instant timestamp,
        String message,
        String path

) {
}
