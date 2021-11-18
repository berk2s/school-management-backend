package com.schoolplus.office.web.models;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatingStudentDto extends CreatingUserDto {

    @Size(max = 99)
    private List<String> parents;

    private Long classRoomId;

}
