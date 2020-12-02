package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.time.LocalDateTime;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;

@ManagedBean
@RequestScoped
public class ArretDAO {

	@Inject
	EntityManager em;

	public void supprimerArret(Itineraire itineraire, Arret arret) {
		// On supprime l'arrêt de l'itinéraire
		em.getTransaction().begin();
		itineraire.getArretsDesservis().remove(arret);
		em.remove(arret);
		em.getTransaction().commit();
	}
	
	public void avancerHeureArriveeEnGare(Arret a, int tempsAvance) {
		LocalDateTime newHeureArriveeEnGare = a.getHeureArriveeEnGare().minusSeconds(tempsAvance);
		em.getTransaction().begin();
		a.setHeureArriveeEnGare(newHeureArriveeEnGare);
		em.getTransaction().commit();
	}
	
	public void retarderHeureArriveeEnGare(Arret a, int tempsRetard) {
		LocalDateTime newHeureArriveeEnGare = a.getHeureArriveeEnGare().plusSeconds(tempsRetard);
		em.getTransaction().begin();
		a.setHeureArriveeEnGare(newHeureArriveeEnGare);
		em.getTransaction().commit();
	}
	
	public void retardHeureDepartDeGare(Arret a, int tempsRetard) {
		LocalDateTime newHeureDepartDeGare = a.getHeureDepartDeGare().plusSeconds(tempsRetard);
		em.getTransaction().begin();
		a.setHeureDepartDeGare(newHeureDepartDeGare);
		em.getTransaction().commit();
	}
}
