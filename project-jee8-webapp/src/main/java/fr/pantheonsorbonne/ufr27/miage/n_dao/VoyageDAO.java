package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyage;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyageur;

@ManagedBean
public class VoyageDAO {

	@Inject
	EntityManager em;

	// On cherche par le nom de la gare ou par une gare carrément ?
	@SuppressWarnings("unchecked")
	public List<Voyage> getVoyagesByNomGareDepart(Gare gareDepart) {
		return (List<Voyage>) em
				.createNativeQuery(
						"SELECT v.* " + "FROM VOYAGE v, GARE g " + "WHERE v.GARREDEPART_ID = g.ID " + "AND g.NOM = ?")
				.setParameter(1, gareDepart.getNom()).getResultList();
	}

	// On cherche par le nom de la gare ou par une gare carrément ?
	@SuppressWarnings("unchecked")
	public List<Voyage> getVoyagesByNomGareArrivee(Gare gareArrivee) {
		return (List<Voyage>) em
				.createNativeQuery(
						"SELECT v.* " + "FROM VOYAGE v, GARE g " + "WHERE v.GARREARRIVEE_ID = g.ID " + "AND g.NOM = ?")
				.setParameter(1, gareArrivee.getNom()).getResultList();
	}

	public Voyage getVoyageByVoyageur(Voyageur voyageur) {
		return (Voyage) em
				.createNativeQuery("SELECT voyage.* " + "FROM VOYAGE voyage, VOYAGEUR voyageur " + "WHERE voyage.ID = ? "
						+ "AND voyageur.ID = ?")
				.setParameter(1, voyageur.getVoyage().getId()).setParameter(2, voyageur.getId()).getSingleResult();
	}
}
