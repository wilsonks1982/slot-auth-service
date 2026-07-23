package org.wilsonks.slotauthservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chaos")
public class ChaosController {

    @GetMapping("/500")
    public void error500() {
        throw new RuntimeException("Simulated failure");
    }

    @GetMapping("/timeout")
    public ResponseEntity<String> timeout() throws InterruptedException {
        Thread.sleep(5000);
        return ResponseEntity.ok("Recovered");
    }
}
