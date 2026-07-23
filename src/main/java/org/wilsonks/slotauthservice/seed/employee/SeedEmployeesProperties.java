package org.wilsonks.slotauthservice.seed.employee;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "seed.employees")
@Getter
@Setter
public class SeedEmployeesProperties {
    private boolean enabled = false;
    private String source;
}
