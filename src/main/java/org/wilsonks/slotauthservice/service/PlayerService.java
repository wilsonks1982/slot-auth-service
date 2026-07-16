package org.wilsonks.slotauthservice.service;


import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wilsonks.slotauthservice.domain.Player;
import org.wilsonks.slotauthservice.dto.player.PlayerResponse;
import org.wilsonks.slotauthservice.repository.PlayerRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PlayerService {
    private final PlayerRepository repository;

    @PostConstruct
    public void init() {
        // Initialize the service, if needed
        log.info("✅ PlayerService initialized");
    }

    @Transactional(readOnly = true)
    public List<PlayerResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(PlayerResponse::fromEntity)
                .toList();
    }
}
