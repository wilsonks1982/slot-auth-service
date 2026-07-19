package org.wilsonks.slotauthservice.service;


import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.wilsonks.slotauthservice.domain.Player;
import org.wilsonks.slotauthservice.dto.player.PlayerResponse;
import org.wilsonks.slotauthservice.dto.player.PlayerSessionResponse;
import org.wilsonks.slotauthservice.repository.PlayerRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PlayerService {

    private final PlayerRepository repository;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;

    @PostConstruct
    public void init() {
        repository.save(new Player(null, "73feb4df", "Player 1", encoder.encode("1234"), false, false, null, null, null));
        repository.save(new Player(null, "d36defe3", "Player 2", encoder.encode("1234"), false, false, null, null, null));
        repository.save(new Player(null, "639447e4", "Player 3", encoder.encode("1234"), false, false, null, null, null));
        repository.save(new Player(null, "d33eb9df", "Player 4", encoder.encode("1234"), false, false, null, null, null));
        repository.save(new Player(null, "03824ee4", "Player 5", encoder.encode("1234"), false, false, null, null, null));
        repository.save(new Player(null, "53eef7e3", "Player 6", encoder.encode("1234"), false, false, null, null, null));
        log.info("✅ PlayerService initialized with default players");
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    public Optional<PlayerSessionResponse> createSession(String uid, String pin) {
        // Fetch the player by UID with a lock for update
        Optional<Player> playerOpt = repository.findByUidForUpdate(uid);
        if (playerOpt.isEmpty()) {
            return Optional.empty();
        }
        // Validate the PIN using the PasswordEncoder
        if (!encoder.matches(pin, playerOpt.get().getPin())) {
            return Optional.empty();
        }

        if(playerOpt.get().getIsPlaying() == true) {
            return Optional.of(
                    PlayerSessionResponse.builder()
                            .success(false)
                            .message("Player is already on session.")
                            .build()
            );
        }

        if (playerOpt.get().getOnHold() == true) {
            return Optional.of(
                    PlayerSessionResponse.builder()
                            .success(false)
                            .message("Player is on hold, please contact staff.")
                            .build()
            );
        }


        Player player = playerOpt.get();
        player.setIsPlaying(true);
        repository.save(player);

        return Optional.of(
                PlayerSessionResponse.builder()
                        .success(true)
                        .message("Player session created successfully.")
                        .uid(player.getUid())
                        .nickname(player.getNickname())
                        .token(jwtService.generatePlayerToken(player.getUid()))
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public List<PlayerResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(PlayerResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<PlayerResponse> findByUid(String uid) {
        return repository.findByUid(uid).map(PlayerResponse::fromEntity);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    public Optional<PlayerResponse> changePin(String uid, String newPin) {
        Optional<Player> playerOpt = repository.findByUidForUpdate(uid);
        if (playerOpt.isEmpty()) {
            return Optional.empty();
        }

        Player player = playerOpt.get();
        player.setPin(newPin);
        repository.save(player);

        return Optional.of(PlayerResponse.fromEntity(player));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    public Optional<PlayerResponse> changeNickname(String uid, String newNickname) {
        Optional<Player> playerOpt = repository.findByUidForUpdate(uid);
        if (playerOpt.isEmpty()) {
            return Optional.empty();
        }

        Player player = playerOpt.get();
        player.setNickname(newNickname);
        repository.save(player);

        return Optional.of(PlayerResponse.fromEntity(player));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    public Optional<PlayerResponse> closeSession(String uid) {
        Optional<Player> playerOpt = repository.findByUidForUpdate(uid);
        if (playerOpt.isEmpty()) {
            return Optional.empty();
        }

        Player player = playerOpt.get();
        player.setIsPlaying(false);
        repository.save(player);

        return Optional.of(PlayerResponse.fromEntity(player));
    }



}
