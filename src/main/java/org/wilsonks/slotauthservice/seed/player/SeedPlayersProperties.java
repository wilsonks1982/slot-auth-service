package org.wilsonks.slotauthservice.seed.player;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "seed.players")
@Setter
@Getter
public class SeedPlayersProperties {
    private boolean enabled = false;
    private String source; // classpath:seed/players.v1.json | file:/etc/seed/players.v1.json
}
