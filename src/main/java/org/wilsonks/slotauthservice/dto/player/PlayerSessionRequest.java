package org.wilsonks.slotauthservice.dto.player;

public record PlayerSessionRequest(
        String uid,
        String pin
) {
}
