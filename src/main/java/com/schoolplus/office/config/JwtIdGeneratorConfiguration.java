package com.schoolplus.office.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.IdGenerator;
import org.springframework.util.JdkIdGenerator;

@Configuration
public class JwtIdGeneratorConfiguration {

    @Bean
    IdGenerator idGenerator() {
        return new JdkIdGenerator();
    }

}
