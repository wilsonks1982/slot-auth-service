package org.wilsonks.slotauthservice.dto;

import org.wilsonks.slotauthservice.domain.Employee;
import org.wilsonks.slotauthservice.domain.EmployeeRole;

public record EmployeeResponse(
        String uid,
        String account,
        String role
) {
    public static EmployeeResponse fromEntity(Employee employee) {
        return new EmployeeResponse(
                employee.getUid(),
                employee.getAccount(),
                employee.getRole().name()
        );
    }
}