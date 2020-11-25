package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Trajet;

@ManagedBean
public class TrajetDAO {

	@Inject
	EntityManager em;

	public List<Trajet> getTrajetsByItineraire(Itineraire itineraire) {
		return (List<Trajet>) em.createNamedQuery("Trajet.getTrajetsByItineraire", Trajet.class)
				.setParameter("idItineraire", itineraire.getId()).getResultList();
	}

	public List<Trajet> getTrajetsByNomGareDeDepart(Gare gareDepart) {
		return (List<Trajet>) em
				.createNamedQuery("Trajet.getTrajetsByNomGareDeDepart", Trajet.class)
				.setParameter("nom", gareDepart.getNom()).getResultList();
	}
	
	public List<Trajet> getTrajetsByNomGareArrivee(Gare gareArrivee) {
		return (List<Trajet>) em
				.createNamedQuery("Trajet.getTrajetsByNomGareArrivee", Trajet.class)
				.setParameter("nom", gareArrivee.getNom()).getResultList();
	}

	public void deleteTrajet(Trajet trajet) {
		em.getTransaction().begin();
		em.remove(trajet);
		em.getTransaction().commit();
	}
}
