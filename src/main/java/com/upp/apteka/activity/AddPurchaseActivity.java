package com.upp.apteka.activity;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.upp.apteka.bo.Pharmacy;
import com.upp.apteka.bo.PharmacyMedicine;
import com.upp.apteka.bo.Prescription;
import com.upp.apteka.bo.PrescriptionMedicine;
import com.upp.apteka.bo.Purchase;
import com.upp.apteka.bo.PurchaseMedicine;
import com.upp.apteka.component.buy.form.BuyInputForm;
import com.upp.apteka.config.Mapper;
import com.upp.apteka.layout.ModifiedFlowLayout;
import com.upp.apteka.service.MedicineService;
import com.upp.apteka.service.PharmacyService;
import com.upp.apteka.service.PurchaseService;

@Component("addPurchaseActivity")
@Scope("prototype")
public class AddPurchaseActivity implements Activity {

	@Autowired
	private JFrame frame;

	@Autowired
	private PurchaseService purchaseService;

	@Autowired
	private MedicineService medicineService;

	@Autowired
	private PharmacyService pharmacyService;

	private List<BuyInputForm> forms;
	
	private JLabel totalPriceLabel;

	@Autowired
	private Long pharmacyId;

	@Autowired
	private Mapper mapper;

	private static final int WINDOW_BORDER = 20;
	private static final int SUBMIT_WIDTH = 100;
	private static final int SUBMIT_HEIGHT = 35;

	public void showActivity(final Map<String, Object> params) {

		final Prescription prescription = (Prescription) params.get("prescription");

		frame.setContentPane(new JPanel());
		frame.setLayout(new BorderLayout());

		final Pharmacy pharmacy = pharmacyService.getPharmacy(pharmacyId);

		JPanel contentPanel = new JPanel();
		contentPanel
				.setBorder(BorderFactory.createEmptyBorder(WINDOW_BORDER, WINDOW_BORDER, WINDOW_BORDER, WINDOW_BORDER));
		contentPanel.setLayout(new BorderLayout(WINDOW_BORDER, WINDOW_BORDER));

		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createTitledBorder("Загальна інформація"));
		mainPanel.setLayout(new GridLayout(0, 1));

		JPanel infoPanel = new JPanel();
		infoPanel.setBorder(BorderFactory.createEmptyBorder(0, WINDOW_BORDER, 0, WINDOW_BORDER));
		infoPanel.setLayout(new GridLayout(0, 1));

		infoPanel.add(new JLabel("<html><b>Покупець</b>: " + prescription.getPatient().getSurname() + " "
				+ prescription.getPatient().getName() + "<br/><b>Лікар</b>: " + prescription.getDoctor().getSurname()
				+ " " + prescription.getDoctor().getName() + "<br/><b>Дата</b>: " + prescription.getDate()
				+ "</html>"));

		totalPriceLabel = new JLabel("<html><b>Вартість</b>: 0");
		infoPanel.add(totalPriceLabel);

		forms = new ArrayList<>();

		mainPanel.add(infoPanel);

		contentPanel.add(mainPanel, BorderLayout.NORTH);

		outer: for (PrescriptionMedicine pm : prescription.getPrescriptionMedicines()) {
			List<PharmacyMedicine> pharmacyMedicines = pm.getMedicine().getPharmacyMedicines();

			for (PharmacyMedicine pharmMedicine : pharmacyMedicines) {
				if (pharmMedicine.getPharmacy().getId() == pharmacy.getId()) {
					BuyInputForm buy = new BuyInputForm(pm.getMedicine().getId(),
							pm.getMedicine().getName() + " " + pm.getMedicine().getProducer(), pm.getPackBought(),
							pm.getPackQuantity(), pharmMedicine.getPackQuantity(), pharmMedicine.getPackPrice());
					forms.add(buy);

					buy.getNumberField().addKeyListener(new KeyListener() {

						@Override
						public void keyTyped(KeyEvent e) {
							// TODO Auto-generated method stub

						}

						@Override
						public void keyReleased(KeyEvent e) {
							updatePrice();
								
						}

						@Override
						public void keyPressed(KeyEvent e) {
						}
					});
					continue outer;
				}
			}
			forms.add(new BuyInputForm(pm.getMedicine().getId(),
					pm.getMedicine().getName() + " " + pm.getMedicine().getProducer(), pm.getPackBought(),
					pm.getPackQuantity(), 0, new BigDecimal(-1)));

		}

		JPanel parentPanel = new JPanel();
		parentPanel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
		parentPanel.setLayout(new GridLayout(0, 1));

		JPanel fieldsInputPanel = new JPanel();
		fieldsInputPanel.setLayout(new ModifiedFlowLayout());

		JScrollPane scroll = new JScrollPane(fieldsInputPanel);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		scroll.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 1));

		for (BuyInputForm form : forms)
			fieldsInputPanel.add(form);

		parentPanel.add(scroll);
		contentPanel.add(parentPanel);

		frame.add(contentPanel);

		JPanel submitPanel = new JPanel();
		JButton submit = new JButton("Купити");
		submit.setPreferredSize(new Dimension(SUBMIT_WIDTH, SUBMIT_HEIGHT));
		submitPanel.add(submit);

		submit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				List<PurchaseMedicine> purchaseMedicines = new ArrayList<>();

				for (BuyInputForm bif : forms) {

					Integer value = bif.getNumber();

					if (value != null && value > 0) {
						PurchaseMedicine purchaseMedicine = new PurchaseMedicine();
						purchaseMedicine.setMedicine(medicineService.getMedicine(bif.getMedicineId()));
						purchaseMedicine.setPackQuantity(value);

						purchaseMedicines.add(purchaseMedicine);
					}

				}

				Purchase purchase = new Purchase();
				purchase.setDate(new Date(System.currentTimeMillis()));
				purchase.setPatient(prescription.getPatient());
				purchase.setPharmacy(pharmacy);
				purchase.setPrescription(prescription);
				purchase.setPurchaseMedicines(purchaseMedicines);

				if (purchaseMedicines.size() > 0) {

					try {
						purchaseService.create(purchase);
					} catch (Exception addingException) {
						addingException.printStackTrace();
						JOptionPane.showMessageDialog(frame,
								new String[] { "Сервіс тимчасово недоступний. Спробуйте, будь ласка, пізніше." },
								"Помилка", JOptionPane.ERROR_MESSAGE);
						return;
					}

					Map<String, Object> newParams = new HashMap<String, Object>();
					newParams.put("prescriptionId", prescription.getId());
					
					mapper.changeActivity("addPurchase", newParams);

					JOptionPane.showMessageDialog(frame, "Успішно здійснено покупку!", "Успішна операція",
							JOptionPane.INFORMATION_MESSAGE);
				}else{
					updatePrice();
				}
			}
		});

		frame.add(submitPanel, BorderLayout.SOUTH);
	}

	protected void updatePrice() {
		try {

			double totalPrice = 0;

			for (BuyInputForm buyInputForm : forms) {
				Integer value;

				try {
					value = buyInputForm.getNumber();
				} catch (Exception numE) {
					value = 0;
				}

				if (value != null && value > 0) {
					totalPrice += buyInputForm.getPrice().doubleValue() * value;
				}

			}

			NumberFormat nf= NumberFormat.getInstance();
			nf.setMaximumFractionDigits(2);
			nf.setMinimumFractionDigits(2);
			nf.setRoundingMode(RoundingMode.HALF_UP);
			
			totalPriceLabel.setText("<html><b>Вартість</b>: " + nf.format(totalPrice));
		} finally {
		}
		
	}

}