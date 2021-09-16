package com.schoolplus.office.repository;

import com.schoolplus.office.domain.ExamResultItem;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ExamResultItemRepository extends PagingAndSortingRepository<ExamResultItem, Long> {
}
