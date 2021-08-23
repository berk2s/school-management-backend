package com.schoolplus.office.repository;

import com.schoolplus.office.domain.TeachingSubject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeachingSubjectRepository extends JpaRepository<TeachingSubject, Long> {

    Optional<TeachingSubject> findBySubjectName(String subjectName);

}
