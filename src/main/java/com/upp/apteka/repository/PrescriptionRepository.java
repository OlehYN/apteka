package com.upp.apteka.repository;

import java.sql.Date;
import java.util.List;

import com.upp.apteka.bo.Prescription;
import com.upp.apteka.utils.repository.IRepository;

public interface PrescriptionRepository extends IRepository<Prescription, Long>{
	List<Prescription> getUnboughtPrescriptions(Long customerId);
	
	List<Prescription> findByQuery(String query, Date start, Date finishDate, boolean or, Boolean sold);
	
	List<Prescription> findByQuery(String query, Date start, Date finishDate, int offset, int limit, boolean or, Boolean sold);

	int count(String query, java.sql.Date startDate, java.sql.Date endDate, boolean b, Boolean sold);
}
