package org.wilsonks.slotauthservice;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@Slf4j
public class SlotAuthServiceApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SlotAuthServiceApplication.class, args);
        String port = context.getEnvironment().getProperty("server.port");

        System.out.println("✅ Slot Auth Service is running on port " + port);
    }

}
