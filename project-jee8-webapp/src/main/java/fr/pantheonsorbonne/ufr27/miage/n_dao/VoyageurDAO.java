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

	@SuppressWarnings("unchecked")
	public List<Voyageur> getVoyageursByVoyage(Voyage v) {
		return (List<Voyageur>) em.createNativeQuery("SELECT v " + "FROM VOYAGEUR v " + "WHERE v.VOYAGE_ID = ? ")
				.setParameter(1, v.getId()).getResultList();
	}

}
