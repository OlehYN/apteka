package com.upp.apteka.repository.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.upp.apteka.bo.Patient;
import com.upp.apteka.repository.PatientRepository;
import com.upp.apteka.utils.repository.AHibernateRepository;

@Repository
@Transactional
public class PatientRepositoryImpl extends AHibernateRepository<Patient, Long> implements PatientRepository{

	@SuppressWarnings("unchecked")
	public List<Patient> getAll(int offset, int limit) {
		return (List<Patient>) createEntityCriteria().setFirstResult(offset).setMaxResults(limit).list();
	}

	public Long create(Patient patient) {
		return add(patient);
	}

	public Patient read(Long key) {
		return get(key);
	}

	public void update(Patient patient) {
		updateEntity(patient);
		
	}

	public boolean delete(Long key) {
		return deleteEntity(key);
	}

}