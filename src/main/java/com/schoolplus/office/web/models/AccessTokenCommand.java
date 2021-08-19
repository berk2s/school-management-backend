package com.schoolplus.office.web.models;

import com.schoolplus.office.security.SecurityUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AccessTokenCommand {
    SecurityUser securityUser;

    List<String> scopes = new ArrayList<>();
}
