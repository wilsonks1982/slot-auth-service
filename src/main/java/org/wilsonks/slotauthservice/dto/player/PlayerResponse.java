package org.wilsonks.slotauthservice.dto.player;

import org.wilsonks.slotauthservice.domain.Player;
import org.wilsonks.slotauthservice.domain.PlayerStatus;

import java.time.Instant;

public record PlayerResponse(
        Long id,
        String uid,
        String nickname,
        PlayerStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static PlayerResponse fromEntity(Player player) {
        return new PlayerResponse(
                player.getId(),
                player.getUid(),
                player.getNickname(),
                player.getStatus(),
                player.getCreatedAt(),
                player.getUpdatedAt()
        );
    }
}
