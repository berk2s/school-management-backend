package com.schoolplus.office.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "classroom_tag")
    private String classRoomTag;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Teacher advisorTeacher;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "classRoom")
    private List<Student> students = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Grade grade;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, optional = false)
    private Organization organization;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp lastModifiedAt;

    @Version
    private Long version;

    public void addStudent(Student student) {
        if(!students.contains(student)) {
            student.setClassRoom(this);
            students.add(student);
        }
    }

    public void removeStudent(Student student) {
        if(students.contains(student)) {
            student.setClassRoom(null);
            students.remove(student);
        }
    }

}
