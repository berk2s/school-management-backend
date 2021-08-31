package com.schoolplus.office.web.controllers;

import com.schoolplus.office.web.models.LoginRequestDto;
import com.schoolplus.office.web.models.LoginResponseDto;
import com.schoolplus.office.services.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
@RequiredArgsConstructor
@RequestMapping(LoginController.ENDPOINT)
@RestController
public class LoginController {

    public static final String ENDPOINT = "/login";

    private final LoginService loginService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDto> handleLogin(@Valid @RequestBody LoginRequestDto loginRequest) {
        return new ResponseEntity<>(loginService.authenticate(loginRequest), HttpStatus.OK);
    }

}
