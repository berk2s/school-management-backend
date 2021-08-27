package com.schoolplus.office.domain;

import com.schoolplus.office.web.models.GradeLevel;
import com.schoolplus.office.web.models.GradeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
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
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "grade_tag")
    private String gradeTag;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade_type")
    private GradeType gradeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade_level")
    private GradeLevel gradeLevel;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Teacher advisorTeacher;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "grade")
    private List<Student> students = new ArrayList<>();

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp lastModifiedAt;

    @Version
    private Long version;

    public void addStudent(Student student) {
        if(!students.contains(student)) {
            student.setGrade(this);
            students.add(student);
        }
    }

    public void removeStudent(Student student) {
        if(students.contains(student)) {
            student.setGrade(null);
            students.remove(student);
        }
    }

}
