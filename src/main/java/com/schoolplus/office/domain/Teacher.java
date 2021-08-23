package com.schoolplus.office.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Teacher extends User {

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "teacher_subjects",
            joinColumns = @JoinColumn(name = "teacher_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "teaching_subject_id", referencedColumnName = "id"))
    private List<TeachingSubject> teachingSubjects = new ArrayList<>();

    public void addTeachingSubject(TeachingSubject teachingSubject) {
        if (!teachingSubjects.contains(teachingSubject) && !teachingSubject.getTeachers().contains(this)) {
            teachingSubject.getTeachers().add(this);
            teachingSubjects.add(teachingSubject);
        }
    }

    public void removeTeachingSubject(TeachingSubject teachingSubject) {
        if (teachingSubjects.contains(teachingSubject) && teachingSubject.getTeachers().contains(this)) {
            teachingSubject.getTeachers().remove(this);
            teachingSubjects.remove(teachingSubject);
        }
    }

}
