package com.schoolplus.office.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Homework {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "homework_description")
    private String homeworkDescription;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Classroom classroom;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Syllabus syllabus;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp lastModifiedAt;

}
