package com.schoolplus.office.domain;

import com.schoolplus.office.web.models.ExamResultItemDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class ExamResult {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, optional = false)
    private Exam exam;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ExamResultItem> examResultItems = new HashSet<>();

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp lastModifiedAt;

    public void addExamResultItem(ExamResultItem examResultItem) {
        if (!this.examResultItems.contains(examResultItem)) {
            examResultItem.setExamResult(this);
            this.examResultItems.add(examResultItem);
        }
    }

    public void removeExamResultItem(ExamResultItem examResultItem) {
        if (this.examResultItems.contains(examResultItem)) {
            examResultItem.setExamResult(null);
            this.examResultItems.remove(examResultItem);
        }
    }
}
