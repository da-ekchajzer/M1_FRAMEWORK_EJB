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
	@SuppressWarnings("unchecked")
	public List<Voyage> getVoyagesByNomGareDepart(Gare gareDepart) {
		return (List<Voyage>) em
				.createNativeQuery(
						"SELECT v " + "FROM VOYAGE v, GARE g " + "WHERE v.GAREDEPART_ID = g.ID " + "AND g.NOM = ?")
				.setParameter(1, gareDepart.getNom()).getResultList();
	}

	// On cherche par le nom de la gare ou par une gare carrément ?
	@SuppressWarnings("unchecked")
	public List<Voyage> getVoyagesByNomGareArrivee(Gare gareArrivee) {
		return (List<Voyage>) em
				.createNativeQuery(
						"SELECT v " + "FROM VOYAGE v, GARE g " + "WHERE v.GAREARRIVEE_ID = g.ID " + "AND g.NOM = ?")
				.setParameter(1, gareArrivee.getNom()).getResultList();
	}
}
