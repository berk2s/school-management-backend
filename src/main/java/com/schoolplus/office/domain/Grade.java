package com.schoolplus.office.domain;

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

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @Column(name = "grade_name")
    private String gradeName;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE}, mappedBy = "grade")
    private List<Classroom> classrooms = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, optional = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private GradeCategory gradeCategory;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp lastModifiedAt;

    public void addClassroom(Classroom classroom) {
        if (!classrooms.contains(classroom)) {
            classroom.setGrade(this);
            this.classrooms.add(classroom);
        }
    }

    public void removeClassroom(Classroom classroom) {
        if (classrooms.contains(classroom)) {
            classroom.setGrade(null);
            this.classrooms.remove(classroom);
        }
    }

}
