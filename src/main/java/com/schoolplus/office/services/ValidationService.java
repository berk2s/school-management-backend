package com.schoolplus.office.services;

import com.schoolplus.office.web.models.ValidationDto;

public interface ValidationService {

    ValidationDto validateUsername(String givenData);

    ValidationDto validatePhoneNumber(String givenData);

    ValidationDto validateEmail(String givenData);

}
