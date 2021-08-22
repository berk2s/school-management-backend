package com.schoolplus.office.domain;

import com.schoolplus.office.web.models.GradeLevel;
import com.schoolplus.office.web.models.GradeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("STUDENT")
@Entity
public class Student extends User {

    @Enumerated(EnumType.STRING)
    private GradeType gradeType;

    @Enumerated(EnumType.STRING)
    private GradeLevel gradeLevel;

}
