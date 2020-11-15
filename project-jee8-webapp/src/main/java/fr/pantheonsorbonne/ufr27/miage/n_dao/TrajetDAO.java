package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyage;

@ManagedBean
public class TrajetDAO {

	@Inject
	EntityManager em;
	
	public List<Trajet> getTrajetsByVoyage(Voyage v) {
		return (List<Trajet>) em.createNativeQuery("SELECT t "
				+ "FROM VOYAGE v, TRAJET t "
				+ "WHERE t.VOYAGE_ID = ?")
				.setParameter(1, v.getId()).getResultList();
	}
	
	public List<Trajet> getVoyagesByNomGareDepart(Gare gareDepart) {
		return (List<Trajet>) em.createNativeQuery("SELECT t "
				+ "FROM TRAJET t, GARE g "
				+ "WHERE t.GARREDEPART_ID = g.ID "
				+ "AND g.NOM = ?")
				.setParameter(1, gareDepart.getNom()).getResultList();
	}
		
	public List<Trajet> getVoyagesByNomGareArrivee(Gare gareArrivee) {
		return (List<Trajet>) em.createNativeQuery("SELECT t "
				+ "FROM TRAJET t, GARE g "
				+ "WHERE t.GARREARRIVEE_ID = g.ID "
				+ "AND g.NOM = ?")
				.setParameter(1, gareArrivee.getNom()).getResultList();
	}
	
	public Trajet getFirstTrajetOfAVoyage(Voyage v) {
		return (Trajet) em.createNativeQuery("SELECT t "
				+ "FROM TRAJET t, VOYAGE v "
				+ "WHERE t.GARREDEPART_ID = ?")
				.setParameter(1, v.getGareDepart().getId()).getSingleResult();
	}
	
	public Trajet getLastTrajetOfAVoyage(Voyage v) {
		return (Trajet) em.createNativeQuery("SELECT t "
				+ "FROM TRAJET t, VOYAGE v "
				+ "WHERE t.GARREARRIVEE_ID = ?")
				.setParameter(1, v.getGareArrivee().getId()).getSingleResult();
	}
	
	public void deleteTrajet(Trajet trajet) {
		em.getTransaction().begin();
		em.remove(trajet);
		em.getTransaction().commit();
	}
}
