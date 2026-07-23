package org.wilsonks.slotauthservice.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.wilsonks.slotauthservice.seed.employee.SeedEmployeeDocument;
import org.wilsonks.slotauthservice.seed.employee.SeedEmployeesProperties;
import org.wilsonks.slotauthservice.seed.player.SeedPlayersDocument;
import org.wilsonks.slotauthservice.seed.player.SeedPlayersProperties;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeedLoader {

    private final SeedPlayersProperties seedPlayersProperties;
    private final SeedEmployeesProperties seedEmployeesProperties;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    public SeedEmployeeDocument loadSeedEmployeesDocument() {
        if (!seedEmployeesProperties.isEnabled()) {
            log.info("Seed employees is disabled");
            return new SeedEmployeeDocument();
        }

        Resource resource = resourceLoader.getResource(seedEmployeesProperties.getSource());

        if (!resource.exists()) {
            log.warn("Seed employees resource does not exist: {}", seedEmployeesProperties.getSource());
            return new SeedEmployeeDocument();
        }

        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, SeedEmployeeDocument.class);
        } catch (IOException e) {
            log.error("Failed to load seed employees from source: {}", seedEmployeesProperties.getSource(), e);
            throw new RuntimeException(e);
        }
    }

    public SeedPlayersDocument loadSeedUsersDocument() {
        boolean isSeedEnabled = seedPlayersProperties.isEnabled();
        String seedFilePath = seedPlayersProperties.getSource();

        if (!isSeedEnabled) {
            log.info("ℹ️ Seed loading is disabled. Skipping seed loading.");
            return new SeedPlayersDocument(); // NullPointerException
        }

        Resource resource = resourceLoader.getResource(seedFilePath);
        if (!resource.exists()) {
            log.error("❌ Seed document not found at path: {}", seedFilePath);
            throw new IllegalStateException("Seed document not found at: " + seedFilePath);
        }

        log.info("📂 Loading seed document from {}", seedFilePath);

        // Try-with-resources to ensure InputStream is closed
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, SeedPlayersDocument.class);
        } catch (IOException e) {
            log.error("❌ Error occurred while parsing seed JSON file: {}", seedFilePath, e);
            throw new RuntimeException("Failed to parse seed document from " + seedFilePath, e);
        }
    }
}
