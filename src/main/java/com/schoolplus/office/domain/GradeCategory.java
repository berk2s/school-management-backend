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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class GradeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "grade_category_name")
    private String gradeCategoryName;

    @OneToMany(mappedBy = "gradeCategory", cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Grade> grades = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, optional = false)
    private Organization organization;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp lastModifiedAt;

    public void addGrade(Grade grade) {
        if(!this.grades.contains(grade)) {
            grade.setGradeCategory(this);
            this.grades.add(grade);
        }
    }

    public void removeGrade(Grade grade) {
        if(this.grades.contains(grade)) {
            grade.setGradeCategory(null);
            this.grades.remove(grade);
        }
    }
}
