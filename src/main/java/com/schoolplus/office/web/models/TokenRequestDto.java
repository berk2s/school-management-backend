package com.schoolplus.office.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.schoolplus.office.annotations.Logable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TokenRequestDto {

    @NotNull
    @JsonProperty("grant_type")
    private String grantType;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("scopes")
    private String scopes;

    @Logable(type = LogableType.NAME)
    @JsonProperty("username")
    private String username;

}
