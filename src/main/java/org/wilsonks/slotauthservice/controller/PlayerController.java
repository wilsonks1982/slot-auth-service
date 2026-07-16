package org.wilsonks.slotauthservice.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wilsonks.slotauthservice.service.PlayerService;

@RestController
@RequestMapping("/api/players")
@AllArgsConstructor
@Slf4j
public class PlayerController {
    private final PlayerService playerService;

    @GetMapping
    ResponseEntity<?> findAllPlayers() {
        log.info("Fetching all players");
        return ResponseEntity
                .status(200) // 200 OK
                .body(playerService.findAll());

    }

}
