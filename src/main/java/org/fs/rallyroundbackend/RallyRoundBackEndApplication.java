package org.fs.rallyroundbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RallyRoundBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(RallyRoundBackEndApplication.class, args);
    }

}
