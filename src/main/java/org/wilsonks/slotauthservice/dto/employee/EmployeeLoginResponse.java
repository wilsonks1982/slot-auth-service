package org.wilsonks.slotauthservice.dto.employee;

public record EmployeeLoginResponse(
        String token,
        String account,
        String role) {
}
