package org.wilsonks.slotauthservice.seed.employee;

import lombok.Getter;
import lombok.Setter;
import org.wilsonks.slotauthservice.domain.EmployeeRole;

@Getter
@Setter
public class SeedEmployee {
    private String uid;
    private String account;
    private String role;
}
