package com.schoolplus.office.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue
    @Column(name = "id", insertable = false, updatable = false)
    private Long id;

    @Column(name = "token")
    private String token;

    @Column(name = "issue_time")
    private LocalDateTime issueTime;

    @Column(name = "not_before")
    private LocalDateTime notBefore;

    @Column(name = "expiry_date_time")
    private LocalDateTime expiryDateTime;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

}
