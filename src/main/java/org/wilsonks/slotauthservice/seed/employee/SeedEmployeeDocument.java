package org.wilsonks.slotauthservice.seed.employee;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SeedEmployeeDocument {
    private String version;
    private List<SeedEmployee> employees = new ArrayList<>();
}
