package com.schoolplus.office.domain;

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
public class Student extends User implements CanAppointment {

    @ManyToMany(fetch = FetchType.LAZY, cascade = {
            CascadeType.MERGE
    })
    @JoinTable(name = "STUDENT_PARENTS",
            joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "parent_id", referencedColumnName = "id"))
    private List<Parent> parents = new ArrayList<>();

    @Column(name = "student_number", unique = true)
    private Long studentNumber;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Classroom classRoom;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "student")
    private List<Appointment> appointments = new ArrayList<>();

    public void addAppointment(Appointment appointment) {
        if(!appointments.contains(appointment)) {
            appointment.setStudent(this);
            appointments.add(appointment);
        }
    }

    public void deleteAppointment(Appointment appointment) {
        if(appointments.contains(appointment)) {
            appointment.setStudent(null);
            appointments.remove(appointment);
        }
    }

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
