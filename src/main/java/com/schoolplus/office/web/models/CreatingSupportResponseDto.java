package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatingSupportResponseDto {

    @NotNull
    private Long supportId;

    @Size(max = 99999)
    private String responseMessage;

    @NotNull
    private String userId;

    @NotNull
    private boolean isLocked;

}
