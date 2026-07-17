package org.wilsonks.slotauthservice.dto.employee;

import org.wilsonks.slotauthservice.domain.Employee;

import java.time.Instant;

public record EmployeeResponse(
        String uid,
        String account,
        String role,
        String pin,
        Instant createdAt,
        Instant updatedAt) {
    public static EmployeeResponse fromEntity(Employee employee) {
        return new EmployeeResponse(
                employee.getUid(),
                employee.getAccount(),
                employee.getRole().name(),
                "****",
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }
}