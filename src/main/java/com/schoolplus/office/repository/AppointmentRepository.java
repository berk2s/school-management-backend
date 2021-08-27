package com.schoolplus.office.repository;

import com.schoolplus.office.domain.Appointment;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.UUID;


public interface AppointmentRepository extends PagingAndSortingRepository<Appointment, UUID>, QueryByExampleExecutor<Appointment> {

}
