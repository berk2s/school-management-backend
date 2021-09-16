package com.schoolplus.office.domain;

import com.schoolplus.office.web.models.FieldType;
import com.schoolplus.office.web.models.ReferenceField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class ExamField {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "exam_field_name")
    private String examFieldName;

    @Column(name = "field_type")
    @Enumerated(EnumType.STRING)
    private FieldType fieldType;

    @Column(name = "is_reference")
    private Boolean isReference;

    @Column(name = "reference_field")
    @Enumerated(EnumType.STRING)
    private ReferenceField referenceField;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private ExamSkeleton examSkeleton;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp lastModifiedAt;

}
