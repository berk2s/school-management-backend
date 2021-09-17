package com.schoolplus.office.repository;

import com.schoolplus.office.domain.TransactionLog;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TransactionLogRepository extends PagingAndSortingRepository<TransactionLog, Long> {



}
