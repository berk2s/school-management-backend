package com.schoolplus.office.domain;

import com.schoolplus.office.web.models.GradeLevel;
import com.schoolplus.office.web.models.GradeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToMany(fetch = FetchType.LAZY, cascade = {
            CascadeType.MERGE
    })
    @JoinTable(name = "STUDENT_PARENTS",
            joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "parent_id", referencedColumnName = "id"))
    private List<Parent> parents = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Grade grade;

    public void addParent(Parent parent) {
        if(!parents.contains(parent) && !parent.getStudents().contains(this)) {
            parent.getStudents().add(this);
            parents.add(parent);
        }
    }

    public void removeParent(Parent parent) {
        if(parents.contains(parent) && parent.getStudents().contains(this)) {
            parent.getStudents().remove(this);
            parents.remove(parent);
        }
    }

}
