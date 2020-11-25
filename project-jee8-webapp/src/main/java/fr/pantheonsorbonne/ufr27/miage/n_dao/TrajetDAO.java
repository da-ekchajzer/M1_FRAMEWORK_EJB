package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyage;

@ManagedBean
public class TrajetDAO {

	@Inject
	EntityManager em;

	@SuppressWarnings("unchecked")
	public List<Trajet> getTrajetsByVoyage(Voyage v) {
		return (List<Trajet>) em.createNativeQuery("SELECT t " + "FROM VOYAGE v, TRAJET t " + "WHERE t.VOYAGE_ID = ?")
				.setParameter(1, v.getId()).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Trajet> getTrajetsByItineraire(Itineraire itineraire) {
		return (List<Trajet>) em
				.createNativeQuery("SELECT t " + "FROM VOYAGE v, TRAJET t " + "WHERE t.ITINERAIRE_ID = ?")
				.setParameter(1, itineraire.getId()).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Trajet> getTrajetsByNomGareDepart(Gare gareDepart) {
		return (List<Trajet>) em
				.createNativeQuery(
						"SELECT t " + "FROM TRAJET t, GARE g " + "WHERE t.GAREDEPART_ID = ? " + "AND g.NOM = ?")
				.setParameter(1, gareDepart.getId()).setParameter(2, gareDepart.getNom()).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Trajet> getTrajetsByNomGareArrivee(Gare gareArrivee) {
		return (List<Trajet>) em
				.createNativeQuery(
						"SELECT t " + "FROM TRAJET t, GARE g " + "WHERE t.GAREARRIVEE_ID = ? " + "AND g.NOM = ?")
				.setParameter(1, gareArrivee.getId()).setParameter(2, gareArrivee.getNom()).getResultList();
	}

	public void deleteTrajet(Trajet trajet) {
		em.getTransaction().begin();
		em.remove(trajet);
		em.getTransaction().commit();
	}
}
