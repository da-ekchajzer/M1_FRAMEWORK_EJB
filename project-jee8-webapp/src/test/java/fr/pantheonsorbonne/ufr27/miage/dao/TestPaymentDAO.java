package fr.pantheonsorbonne.ufr27.miage.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.pantheonsorbonne.ufr27.miage.conf.PersistenceConf;
import fr.pantheonsorbonne.ufr27.miage.jpa.Address;
import fr.pantheonsorbonne.ufr27.miage.jpa.Contract;
import fr.pantheonsorbonne.ufr27.miage.jpa.Customer;
import fr.pantheonsorbonne.ufr27.miage.jpa.Invoice;
import fr.pantheonsorbonne.ufr27.miage.jpa.Payment;
import fr.pantheonsorbonne.ufr27.miage.service.PaymentService;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@EnableWeld
public class TestPaymentDAO {
	@WeldSetup
	private WeldInitiator weld = WeldInitiator.from(PaymentDAO.class, TestPersistenceProducer.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;

	@Inject
	PaymentDAO dao;

	Payment payment;
	Customer customer;

	@BeforeEach
	public void setup() {

		
		System.out.println("****************** " + dao.toString());
		
		em.getTransaction().begin();

		Address add = new Address();
		add.setCountry("France");
		add.setStreeNumber(12);
		add.setStreetName("rue du chat");
		add.setZipCode("33000");
		em.persist(add);

		Invoice invoice = new Invoice();
		invoice.setDate(new Date());
		invoice.setPayed(false);
		em.persist(invoice);

		payment = new Payment();
		payment.setAmount(120.00);
		payment.setInvoices(Collections.singleton(invoice));

		em.persist(payment);

		Contract contract = new Contract();
		contract.setInvoices(Collections.singleton(invoice));
		invoice.setContract(contract);
		em.persist(contract);

		customer = new Customer();
		customer.setActive(true);
		customer.setAddress(add);
		customer.setFname("Nicolas");
		customer.setLname("Herbaut");
		customer.setContracts(Collections.singleton(contract));
		em.persist(customer);
		
		contract.setCustomer(customer);
		

		em.getTransaction().commit();

	}

	@Test
	public void testPaymentDAO() {

		assertFalse(dao.isPaymentValidated(payment.getId()));

		em.getTransaction().begin();
		payment.setValidated(true);
		em.merge(payment);
		em.getTransaction().commit();

		assertTrue(dao.isPaymentValidated(payment.getId()));

	}

	@Test
	public void testPaymentDAO2() {
	
		List<Payment> payments = dao.getPaymentsForUser(customer.getId());

		
		assertEquals(1, payments.size());
		assertEquals(payment, payments.get(0));

	}

}
