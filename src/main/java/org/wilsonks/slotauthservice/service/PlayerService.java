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
import org.wilsonks.slotauthservice.seed.SeedLoader;
import org.wilsonks.slotauthservice.seed.player.SeedPlayersDocument;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PlayerService {

    private final PlayerRepository repository;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final SeedLoader seedLoader;

    @PostConstruct
    public void init() {
        if (repository.count() == 0) {
            log.info("No players found in the database. Seeding initial player data...");

            try {
                SeedPlayersDocument seedPlayersDocument = seedLoader.loadSeedUsersDocument();

                if(seedPlayersDocument != null && seedPlayersDocument.getPlayers() != null) {
                    AtomicInteger index = new AtomicInteger(0);

                    List<Player> players = seedPlayersDocument.getPlayers().stream()
                            .map(seedPlayer -> {
                                Player player = new Player();
                                player.setUid(seedPlayer.getPlayerUid());
                                player.setNickname("Player " + index.incrementAndGet());// Default PIN for seeded players
                                player.setPin(encoder.encode("1234"));
                                return player;
                            })
                            .toList();
                    if (!players.isEmpty()) {
                        repository.saveAll(players);
                        log.info("✅ PlayerService initialized with {} default players", players.size());
                    } else {
                        log.warn("No players found in the seed document. Check the seed file for player data.");
                    }
                } else {
                    log.warn("No players found in the seed document.");
                }


            } catch (Exception e) {
                log.error("Error occurred while seeding player data: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to seed player data", e);
            }

        }

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


    public Optional<PlayerResponse> holdCard(String uid) {
        Optional<Player> playerOpt = repository.findByUidForUpdate(uid);
        if (playerOpt.isEmpty()) {
            return Optional.empty();
        }

        Player player = playerOpt.get();
        player.setOnHold(true);
        repository.save(player);

        return Optional.of(PlayerResponse.fromEntity(player));
    }

    public Optional<PlayerResponse> unholdCard(String uid) {
        Optional<Player> playerOpt = repository.findByUidForUpdate(uid);
        if (playerOpt.isEmpty()) {
            return Optional.empty();
        }

        Player player = playerOpt.get();
        player.setOnHold(false);
        repository.save(player);

        return Optional.of(PlayerResponse.fromEntity(player));
    }
}
