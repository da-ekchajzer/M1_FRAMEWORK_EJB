package fr.pantheonsorbonne.ufr27.miage.n_service;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;

public class BDDFillerService {
	@Inject
	EntityManager manager;
	
	public void fill() {
		manager.persist(new Gare());
	}
}
