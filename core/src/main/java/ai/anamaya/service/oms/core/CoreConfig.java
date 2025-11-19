package ai.anamaya.service.oms.core;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("ai.anamaya.service.oms.core.repository")
@EntityScan("ai.anamaya.service.oms.core.entity")
public class CoreConfig {}
