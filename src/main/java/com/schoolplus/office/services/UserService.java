package com.schoolplus.office.services;

import com.schoolplus.office.web.models.EditingUserDto;
import com.schoolplus.office.web.models.UserDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserDto getUser(UUID userId);

    List<UserDto> listUsers(Pageable pageable);

    void editUser(UUID userId, EditingUserDto editUser);

    void deleteUser(UUID userId);

}
