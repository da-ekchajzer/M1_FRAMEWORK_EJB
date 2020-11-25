package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyage;

@ManagedBean
public class VoyageDAO {

	@Inject
	EntityManager em;

	// On cherche par le nom de la gare ou par une gare carrément ?
	public List<Voyage> getVoyagesByNomGareDepart(Gare gareDepart) {
		return (List<Voyage>) em.createNamedQuery("Voyage.getVoyagesByGareDeDepart", Voyage.class)
				.setParameter("nom", gareDepart.getNom()).getResultList();
	}

	// On cherche par le nom de la gare ou par une gare carrément ?
	public List<Voyage> getVoyagesByNomGareArrivee(Gare gareArrivee) {
		return (List<Voyage>) em.createNamedQuery("Voyage.getVoyagesByGareArrivee", Voyage.class)
				.setParameter("nom", gareArrivee.getNom()).getResultList();
	}
}
