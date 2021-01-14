package fr.pantheonsorbonne.ufr27.miage.dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.jpa.Voyage;

@ManagedBean
@RequestScoped
public class VoyageDAO {

	@Inject
	EntityManager em;

	/**
	 * Récupérer tous les voyages existants en BD
	 * @return
	 */
	public List<Voyage> getAllVoyages() {
		return (List<Voyage>) em.createNamedQuery("Voyage.getAllVoyages", Voyage.class).getResultList();
	}
}
