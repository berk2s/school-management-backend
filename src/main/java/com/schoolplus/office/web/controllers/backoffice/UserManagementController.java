package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.UserService;
import com.schoolplus.office.web.models.EditUserDto;
import com.schoolplus.office.web.models.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(UserManagementController.ENDPOINT)
@RestController
public class UserManagementController {

    public static final String ENDPOINT = "/management/users";

    private final UserService userService;

    @GetMapping(value = {"/{userId}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> getUser(@Valid @PathVariable UUID userId) {
        return new ResponseEntity<>(userService.getUser(userId), HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDto>> listUsers(@RequestParam(defaultValue = "0") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(userService.listUsers(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @PutMapping(value = {"/{userId}"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateUser(@Valid @PathVariable UUID userId,
                                     @Valid @RequestBody EditUserDto editUserDto) {
        userService.editUser(userId, editUserDto);

        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, ENDPOINT + "/" + userId)
                .build();
    }

    @DeleteMapping(value = {"/{userId}"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@Valid @PathVariable UUID userId) {
        userService.deleteUser(userId);
    }

}
