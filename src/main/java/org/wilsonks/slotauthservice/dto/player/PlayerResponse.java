package org.wilsonks.slotauthservice.dto.player;

import org.wilsonks.slotauthservice.domain.Player;

import java.time.Instant;

public record PlayerResponse(
        Long id,
        String uid,
        String role,
        String nickname,
        String firstName,
        String lastName,
        Long wallet,
        Boolean onHold,
        Integer pin,
        Boolean isPlaying,
        Instant createdAt,
        Instant updatedAt
) {
    public static PlayerResponse fromEntity(Player player) {
        return new PlayerResponse(
                player.getId(),
                player.getUid(),
                "Player",
                player.getNickname(),
                player.getNickname(),
                player.getNickname(),
                0L,
                player.getOnHold(),
                1234,
                player.getIsPlaying(),
                player.getCreatedAt(),
                player.getUpdatedAt()
        );
    }
}
