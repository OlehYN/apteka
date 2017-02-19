package com.upp.apteka.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.upp.apteka.activity.Activity;

@Component("selectPrescription")
public class SelectPrescriptionController implements SwingController {

	@Autowired
	private ApplicationContext appContext;

	public void switchToActivity(Map<String, Object> params) {
		Activity spa = (Activity) appContext.getBean("selectPrescriptionActivity");
		spa.showActivity(new HashMap<String, Object>());
	}
}
