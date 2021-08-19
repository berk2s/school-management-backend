package com.schoolplus.office.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("school-plus.auth-config")
@Component
public class ServerConfiguration {

    @Value("url")
    private String serverUrl;

    private TokenConfiguration accessToken;

    private TokenConfiguration refreshToken;

}
