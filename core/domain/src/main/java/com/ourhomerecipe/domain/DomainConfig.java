package com.ourhomerecipe.domain;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("com.ourhomerecipe.domain")
@EnableJpaRepositories("com.ourhomerecipe.domain")
public class DomainConfig {
}
