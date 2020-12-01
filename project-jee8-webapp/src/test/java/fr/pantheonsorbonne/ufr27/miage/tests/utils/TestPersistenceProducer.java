package fr.pantheonsorbonne.ufr27.miage.tests.utils;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class TestPersistenceProducer {

	@RequestScoped
	@Produces
	public EntityManager getEM(EntityManagerFactory factory) {
		return factory.createEntityManager();
	}

	@Produces
	public EntityManagerFactory getEMF() {
		return Persistence.createEntityManagerFactory("test");
	}

}
