package org.wilsonks.slotauthservice.dto.player;

import lombok.Builder;

@Builder
public record PlayerSessionResponse(
        Boolean success,
        String token,
        String uid,
        String nickname,
        String message
) {
}
