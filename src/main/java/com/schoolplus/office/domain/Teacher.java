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
    private List<Classroom> responsibleClassrooms = new ArrayList<>();

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

    public void addClassroom(Classroom classRoom) {
        if (!responsibleClassrooms.contains(classRoom)) {
            classRoom.setAdvisorTeacher(this);
            responsibleClassrooms.add(classRoom);
        }
    }

    public void removeClassroom(Classroom classRoom) {
        if (responsibleClassrooms.contains(classRoom)) {
            classRoom.setAdvisorTeacher(null);
            responsibleClassrooms.remove(classRoom);
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
