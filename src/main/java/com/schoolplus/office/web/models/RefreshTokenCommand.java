package com.schoolplus.office.web.models;

import com.schoolplus.office.security.SecurityUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RefreshTokenCommand {

    SecurityUser securityUser;

    Duration expiryDateTime;

    Duration notBefore;

}
