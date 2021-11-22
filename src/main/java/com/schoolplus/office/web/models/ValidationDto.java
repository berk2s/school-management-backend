package com.schoolplus.office.web.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidationDto {

    private Boolean isTaken;

}
