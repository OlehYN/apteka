package com.upp.apteka.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mysql.jdbc.StringUtils;
import com.upp.apteka.dto.PatientDto;
import com.upp.apteka.service.PatientService;

@Component
@Scope("prototype")
public class PatientValidator implements Validator {
	
	@Autowired
	private PatientService patientService;

	public List<ValidationError> validate(Object target) {

		List<ValidationError> errors = new ArrayList<ValidationError>();
		PatientDto patientDto = (PatientDto) target;

		if (StringUtils.isEmptyOrWhitespaceOnly(patientDto.getSurname()))
			errors.add(new ValidationError("error:surname", "Потрібно вказати прізвище."));
		else if (patientDto.getSurname().length() > 50)
			errors.add(new ValidationError("error:surname", "Занадто довге прізвище."));

		if (StringUtils.isEmptyOrWhitespaceOnly(patientDto.getName()))
			errors.add(new ValidationError("error:name", "Потрібно вказати ім'я."));
		else if (patientDto.getName().length() > 50)
			errors.add(new ValidationError("error:name", "Занадто довге ім'я."));

		Pattern pattern = Pattern.compile("^[0-9]{3}-[0-9]{3}-[0-9]{4}$");
		Matcher matcher = pattern.matcher(patientDto.getPhone());

		if (!matcher.matches()) {
			errors.add(new ValidationError("error:phone", "Потрібно вказати номер у форматі ХХХ-ХХХ-ХХХХ"));
		}else if(patientService.containsNumber(patientDto.getPhone()))
			errors.add(new ValidationError("error:phone", "Такий номер вже використовується"));

		return errors;
	}
}
