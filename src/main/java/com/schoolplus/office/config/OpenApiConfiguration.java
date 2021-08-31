package com.schoolplus.office.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI openAPI(@Value("${springdoc.version}") String appVersion,
                           @Value("${school-plus.appName}") String appName) {
        return new OpenAPI().info(new Info()
                .title(appName + " API")
                .version(appVersion)
                .description("API endpoints detailed descriptions"));
    }

}
