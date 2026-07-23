package org.wilsonks.slotauthservice.seed.player;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SeedPlayersDocument {
    private String version;
    private List<SeedPlayer> players = new ArrayList<>();
}
