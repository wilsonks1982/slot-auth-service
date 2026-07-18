package org.wilsonks.slotauthservice.dto.employee;

public record EmployeeLoginResponse(
        String token,
        String uid,
        String account,
        String role,
        Long expiresIn
        ) {
}
