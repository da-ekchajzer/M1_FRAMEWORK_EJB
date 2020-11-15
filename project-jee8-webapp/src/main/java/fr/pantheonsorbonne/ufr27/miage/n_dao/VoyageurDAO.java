package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyage;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyageur;

@ManagedBean
public class VoyageurDAO {

	@Inject
	EntityManager em;
	
	public List<Voyageur> getAllVoyageursByVoyage(Voyage v) {
		return em.createNativeQuery("SELECT voyageur "
				+ "FROM VOYAGEUR voyageur "
				+ "WHERE voyageur.VOYAGE_ID = ? ")
				.setParameter(1, v.getId()).getResultList();
	}
	
}
