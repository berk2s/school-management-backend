package com.schoolplus.office.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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

}
