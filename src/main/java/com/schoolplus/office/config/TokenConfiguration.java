package com.schoolplus.office.config;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
public class TokenConfiguration {
    Duration lifetime;
}
