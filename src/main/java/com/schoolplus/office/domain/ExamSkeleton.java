package com.schoolplus.office.domain;

import com.schoolplus.office.web.models.ExamSkeletonDto;
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
public class ExamSkeleton {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "exam_skeleton_name")
    private String examSkeletonName;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "examSkeleton")
    private List<ExamField> examFields = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "examSkeleton")
    private List<Exam> exams = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, optional = false)
    private Organization organization;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp lastModifiedAt;

    public void addExamField(ExamField examField) {
        if (!this.examFields.contains(examField)) {
            examField.setExamSkeleton(this);
            this.examFields.add(examField);
        }
    }

    public void removeExamField(ExamField examField) {
        if (this.examFields.contains(examField)) {
            examField.setExamSkeleton(null);
            this.examFields.remove(examField);
        }
    }

}
