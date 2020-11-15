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
	public List<Voyage> getVoyagesByNomGareDepart(Gare gareDepart) {
		return em.createNativeQuery("SELECT v "
				+ "FROM VOYAGE v, GARE g "
				+ "WHERE v.GARREDEPART_ID = g.ID "
				+ "AND g.NOM = ?")
				.setParameter(1, gareDepart.getNom()).getResultList();
	}
	
	// On cherche par le nom de la gare ou par une gare carrément ?
	public List<Voyage> getVoyagesByNomGareArrivee(Gare gareArrivee) {
		return em.createNativeQuery("SELECT v "
				+ "FROM VOYAGE v, GARE g "
				+ "WHERE v.GARREARRIVEE_ID = g.ID "
				+ "AND g.NOM = ?")
				.setParameter(1, gareArrivee.getNom()).getResultList();
	}
	
	public List<Voyage> getVoyagesByVoyageur(Voyageur voyageur) {
		return em.createNativeQuery("SELECT v "
				+ "FROM VOYAGE voyage, VOYAGEUR voyageur "
				+ "WHERE voyage.ID = voyageur.VOYAGE_ID "
				+ "AND voyageur.NOM = ? "
				+ "AND voyageur.PRENOM = ?")
				.setParameter(1, voyageur.getNom())
				.setParameter(2, voyageur.getPrenom()).getResultList();
	}
}
