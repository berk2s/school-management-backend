package com.schoolplus.office.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class ExamResultItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private ExamResult examResult;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Classroom classroom;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Student student;

    @Column(name = "sortable")
    private BigDecimal sortable;

    @ElementCollection
    @CollectionTable(name = "result_item_data",
            joinColumns = {@JoinColumn(name = "result_item_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "row_name")
    @Column(name = "result_data")
    private Map<String, String> resultData = new HashMap<>();

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp lastModifiedAt;

    public void addResultData(String key, String value) {
        if (!this.resultData.containsKey(key)) {
            this.resultData.put(key, value);
        }
    }

    public void removeResultData(String key) {
        this.resultData.remove(key);
    }

}
