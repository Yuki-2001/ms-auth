package com.user.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.user.authentication.model.EmployeeDetails;

@Repository
public interface IEmployeeDetailsRepository
		extends JpaRepository<EmployeeDetails, String>, JpaSpecificationExecutor<EmployeeDetails> {

}
