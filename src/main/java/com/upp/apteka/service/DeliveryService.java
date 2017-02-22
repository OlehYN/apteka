package com.upp.apteka.service;

import java.sql.Date;
import java.util.List;

import com.upp.apteka.bo.Delivery;
import com.upp.apteka.bo.DeliveryMedicine;
import com.upp.apteka.dto.ChooseMedicineDto;

public interface DeliveryService {

	//get all deliveries to all pharmacies ordered by date (the most recent dates first)
	List<Delivery> getAllDeliveries(int offset);
	
	//get all pharmacy deliveries ordered by date (the most recent dates first)
	List<Delivery> getAllPharmacyDeliveries(Long pharmacyId, int offset);
	
	Delivery getDelivery(Long id);
	
	//add new Delivery
	Long addDelivery(Delivery delivery);
	Long addDelivery(Date date, List<ChooseMedicineDto> dtos);
	
	void updateDelivery(Delivery delivery);
	
	//delete delivery if no one medicine from the delivery medicines was sold
	boolean deleteDelivery(Long id);
	boolean checkIfDeliveryMedicineSold(Long deliveryId);
	
	List<Delivery> getPharmacyDeliveriesByPeriod(Date from, Date to, Long pharmacyId, int offset);
	
	// ?
	List<Delivery> getPharmacyMedicineDeliveriesByPeriod(Date from, Date to, Long pharmacyId, Long medicineId, int offset);
	
	List<DeliveryMedicine> getDeliveryMedicines(Long deliveryId, int offset, int limit);
	
	int countDM(Long deliveryId);
	
	List<Delivery> findByQuery(String query, Date start, Date finish, int offset, int limit, boolean or);

	int count(String query, Date start, Date finish, boolean or);

}
