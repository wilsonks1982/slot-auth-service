package org.wilsonks.slotauthservice.dto;

public record EmployeeLoginResponse(
        String token,
        String account,
        String role) {
}
