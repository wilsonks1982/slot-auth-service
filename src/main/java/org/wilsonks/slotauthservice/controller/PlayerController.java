package org.wilsonks.slotauthservice.controller;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wilsonks.slotauthservice.dto.player.*;
import org.wilsonks.slotauthservice.service.PlayerService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/players")
@AllArgsConstructor
@Slf4j
public class PlayerController {
    private final PlayerService playerService;

    @PostMapping("/session")
    ResponseEntity<?> createPlayerSession(@RequestBody @Valid PlayerSessionRequest request) {
        log.info("Creating player session");

        Optional<PlayerSessionResponse> playerSessionOpt = playerService.createSession(request.uid(), request.pin());

        if (playerSessionOpt.isEmpty()) {
            return ResponseEntity
                    .status(401) // 401 Unauthorized
                    .body("Invalid UID or PIN");
        }

        PlayerSessionResponse playerSession = playerSessionOpt.get();

        if (!playerSession.success()) {
            return ResponseEntity
                    .status(409) // 409 Conflict
                    .body(playerSession.message());
        }

        log.info("Player session created successfully for UID: {}", request.uid());
        return ResponseEntity
                .status(200) // 200 OK
                .body(playerSession);

    }

    @PatchMapping("/{uid}/session/close")
    ResponseEntity<?> closePlayerSession(@PathVariable String uid) {
        log.info("Closing player session for UID: {}", uid);

        Optional<PlayerResponse> updatedPlayer = playerService.closeSession(uid);

        if (updatedPlayer.isEmpty()) {
            return ResponseEntity
                    .status(404) // 404 Not Found
                    .body("Player not found");
        }

        log.info("Player session closed successfully for UID: {}", uid);
        return ResponseEntity
                .status(200) // 200 OK
                .body(updatedPlayer.get());
    }

    @PatchMapping("/{uid}/card/hold")
    ResponseEntity<?> holdPlayerCard(@PathVariable String uid) {
        log.info("Putting player card on hold for UID: {}", uid);

        Optional<PlayerResponse> updatedPlayer = playerService.holdCard(uid);

        if (updatedPlayer.isEmpty()) {
            return ResponseEntity
                    .status(404) // 404 Not Found
                    .body("Player not found");
        }

        log.info("Player card put on hold successfully for UID: {}", uid);
        return ResponseEntity
                .status(200) // 200 OK
                .body(updatedPlayer.get());
    }


    @PatchMapping("/{uid}/card/unhold")
    ResponseEntity<?> unholdPlayerCard(@PathVariable String uid) {
        log.info("Removing hold from player card for UID: {}", uid);

        Optional<PlayerResponse> updatedPlayer = playerService.unholdCard(uid);

        if(updatedPlayer.isEmpty()) {
            return ResponseEntity
                    .status(404) // 404 Not Found
                    .body("Player not found");
        }

        log.info("Hold removed from player card successfully for UID: {}", uid);
        return ResponseEntity
                .status(200) // 200 OK
                .body(updatedPlayer.get());
    }


    @PatchMapping("/{uid}/pin/change")
    ResponseEntity<?> changePlayerPin(@PathVariable String uid, @RequestBody @Valid UpdatePinRequest request) {
        log.info("Changing player PIN");

        Optional<PlayerResponse> updatedPlayer = playerService.changePin(uid, request.pin());

        if (updatedPlayer.isEmpty()) {
            return ResponseEntity
                    .status(404) // 404 Not Found
                    .body("Player not found");
        }

        log.info("Player PIN changed successfully for UID: {}", uid);
        return ResponseEntity
                .status(200) // 200 OK
                .body(updatedPlayer.get());
    }

    @PatchMapping("/{uid}/nickname/change")
    ResponseEntity<?> changePlayerNickname(@PathVariable String uid, @RequestBody @Valid UpdateNicknameRequest request) {
        log.info("Changing player nickname");

        Optional<PlayerResponse> updatedPlayer = playerService.changeNickname(uid, request.nickname());

        if (updatedPlayer.isEmpty()) {
            return ResponseEntity
                    .status(404) // 404 Not Found
                    .body("Player not found");
        }

        log.info("Player nickname changed successfully for UID: {}", uid);
        return ResponseEntity
                .status(200) // 200 OK
                .body(updatedPlayer.get());
    }

    @GetMapping
    ResponseEntity<?> findAllPlayers() {
        log.info("Fetching all players");
        List<PlayerResponse> players = playerService.findAll();

        return ResponseEntity
                .status(200) // 200 OK
                .body(players);
    }

    @GetMapping("/{uid}")
    ResponseEntity<?> findPlayerByUid(@PathVariable String uid) {
        log.info("Fetching player by UID: {}", uid);
        Optional<PlayerResponse> playerOpt = playerService.findByUid(uid);

        if (playerOpt.isEmpty()) {
            return ResponseEntity
                    .status(404) // 404 Not Found
                    .body("Player not found");
        }

        return ResponseEntity
                .status(200) // 200 OK
                .body(playerOpt.get());
    }





}
