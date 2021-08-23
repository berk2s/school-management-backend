package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditingParentDto extends EditUserDto {

    public List<String> addedStudents = new ArrayList<>();

    public List<String> deletedStudents = new ArrayList<>();

}
