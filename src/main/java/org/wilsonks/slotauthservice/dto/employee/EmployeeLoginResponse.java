package org.wilsonks.slotauthservice.dto.employee;

import java.time.Instant;

public record EmployeeLoginResponse(
        String token,
        String uid,
        String account,
        String role,
        Long expiresIn,
        Instant lastReset,
        Instant createdAt,
        Instant updatedAt

        ) {
}
