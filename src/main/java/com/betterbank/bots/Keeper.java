package com.betterbank.bots;

import com.betterbank.bots.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
@EnableScheduling
@EnableAsync
public class Keeper {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Keeper.class);
        app.run(args);
    }
}
