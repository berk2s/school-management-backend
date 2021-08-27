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
@DiscriminatorValue("TEACHER")
@Entity
public class Teacher extends User implements CanAppointment {

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "teacher_subjects",
            joinColumns = @JoinColumn(name = "teacher_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "teaching_subject_id", referencedColumnName = "id"))
    private List<TeachingSubject> teachingSubjects = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "advisorTeacher")
    private List<Grade> responsibleGrades = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "teacher")
    private List<Appointment> appointments = new ArrayList<>();

    public void addAppointment(Appointment appointment) {
        if (!appointments.contains(appointment)) {
            appointment.setTeacher(this);
            appointments.add(appointment);
        }
    }

    public void removeAppointment(Appointment appointment) {
        if (appointments.contains(appointment)) {
            appointment.setTeacher(null);
            appointments.remove(appointment);
        }
    }

    public void addGrade(Grade grade) {
        if (!responsibleGrades.contains(grade)) {
            grade.setAdvisorTeacher(this);
            responsibleGrades.add(grade);
        }
    }

    public void removeGrade(Grade grade) {
        if (responsibleGrades.contains(grade)) {
            grade.setAdvisorTeacher(null);
            responsibleGrades.remove(grade);
        }
    }

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
