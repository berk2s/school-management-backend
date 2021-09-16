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
public class SupportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "supportRequest")
    private List<SupportThread> supportThreads = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Organization organization;

    @Column(name = "is_seen")
    private Boolean isSeen;

    @Column(name = "is_locked")
    private Boolean isLocked;

    @Column(name = "is_anonymous")
    private Boolean isAnonymous;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp lastModifiedAt;

    public void addThread(SupportThread supportThread) {
        if (!this.supportThreads.contains(supportThread)) {
            this.supportThreads.add(supportThread);
            supportThread.setSupportRequest(this);
        }
    }

    public void removeThread(SupportThread supportThread) {
        if (this.supportThreads.contains(supportThread)) {
            this.supportThreads.remove(supportThread);
            supportThread.setSupportRequest(null);
        }
    }

}
