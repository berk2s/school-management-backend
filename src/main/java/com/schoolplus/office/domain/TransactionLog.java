package com.schoolplus.office.domain;

import com.schoolplus.office.web.models.DomainAction;
import com.schoolplus.office.web.models.TransactionDomain;
import com.schoolplus.office.web.models.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class TransactionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "transaction_domain")
    @Enumerated(EnumType.STRING)
    private TransactionDomain transactionDomain;

    @Column(name = "domain_action_type")
    @Enumerated(EnumType.STRING)
    private DomainAction domainActionType;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private User performedBy;

    @Column(name = "related_id")
    private String relatedId;

    @CreationTimestamp
    private Timestamp createdAt;

    @LastModifiedDate
    private Timestamp lastModifiedAt;

}
