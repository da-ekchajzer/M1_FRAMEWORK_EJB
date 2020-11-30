package fr.pantheonsorbonne.ufr27.miage.dao;

import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.jpa.Payment;

@ManagedBean
public class PaymentDAO {

	@RequestScoped
	@Inject
	EntityManager manager;

	public boolean isPaymentValidated(int paymentId) {

		Payment p = manager.find(Payment.class, paymentId);
		if (p == null) {
			throw new NoSuchElementException("No Such Payment");
		}
		return p.isValidated();

	}

	public List<Payment> getPaymentsForUser(int userId) {
		List<Payment> res = manager.createNamedQuery("getPaymentForCustomer").setParameter("id", userId)
				.getResultList();
		return res;

	}

}
