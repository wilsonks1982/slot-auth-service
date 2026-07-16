package org.wilsonks.slotauthservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.wilsonks.slotauthservice.domain.EmployeeRole;

public record EmployeeUpdateRequest(
        @NotBlank @Size(min = 1, max = 8)
        String uid,

        @NotBlank @Size(min = 1, max = 20)
        String account,

        @NotBlank @Size(min = 4, max = 4)
        String pin,

        @NotNull
        EmployeeRole role) {
}
