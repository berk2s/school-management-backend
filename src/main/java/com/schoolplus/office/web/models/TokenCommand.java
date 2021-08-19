package com.schoolplus.office.web.models;

import com.schoolplus.office.security.SecurityUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TokenCommand {
    SecurityUser securityUser;

    String audience;

    Duration expiryDateTime;

    Duration notBeforeTime;

    Map<String, Object> claims = new HashMap<>();
}
