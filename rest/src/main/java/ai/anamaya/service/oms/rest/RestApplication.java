package ai.anamaya.service.oms.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "ai.anamaya.service.oms.core.repository")
@EntityScan(basePackages = "ai.anamaya.service.oms.core.entity")
@SpringBootApplication(scanBasePackages = "ai.anamaya.service.oms")
public class RestApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }
}
