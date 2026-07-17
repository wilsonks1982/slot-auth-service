package org.wilsonks.slotauthservice.dto.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmployeeLoginRequest(
        @NotBlank @Size(min = 1, max = 20)
        String account,

        @NotBlank @Size(min = 4, max = 4)
        String pin
) {
}
