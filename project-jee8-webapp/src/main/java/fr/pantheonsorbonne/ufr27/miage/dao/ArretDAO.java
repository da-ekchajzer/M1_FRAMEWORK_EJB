package fr.pantheonsorbonne.ufr27.miage.dao;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;

@ManagedBean
@RequestScoped
public class ArretDAO {

	@Inject
	EntityManager em;

	/**
	 * Supprimer un arrêt en BD
	 * @param arret
	 */
	public void supprimerArret(Arret arret) {
		em.getTransaction().begin();
		em.remove(arret);
		em.getTransaction().commit();
	}

	/**
	 * Avancer l'heure d'arrivée en gare de tempsAvance seconde(s)
	 * pour l'arrêt passé en paramètre
	 * @param a
	 * @param tempsAvance
	 */
	public void avancerHeureArriveeEnGare(Arret a, int tempsAvance) {
		LocalDateTime newHeureArriveeEnGare = a.getHeureArriveeEnGare().minusSeconds(tempsAvance);
		em.getTransaction().begin();
		a.setHeureArriveeEnGare(newHeureArriveeEnGare);
		em.getTransaction().commit();
	}

	/**
	 * Avancer l'heure de départ de gare de tempsAvance seconde(s)
	 * pour l'arrêt passé en paramètre
	 * @param a
	 * @param tempsAvance
	 */
	public void avancerHeureDepartDeGare(Arret a, int tempsAvance) {
		LocalDateTime newHeureDepartDeGare = a.getHeureDepartDeGare().minusSeconds(tempsAvance);
		em.getTransaction().begin();
		a.setHeureDepartDeGare(newHeureDepartDeGare);
		em.getTransaction().commit();
	}

	/**
	 * Reculer l'heure d'arrivée en gare de tempsAvance seconde(s)
	 * pour l'arrêt passé en paramètre
	 * @param a
	 * @param tempsAvance
	 */
	public void retarderHeureArriveeEnGare(Arret a, int tempsRetard) {
		LocalDateTime newHeureArriveeEnGare = a.getHeureArriveeEnGare().plusSeconds(tempsRetard);
		em.getTransaction().begin();
		a.setHeureArriveeEnGare(newHeureArriveeEnGare);
		em.getTransaction().commit();
	}

	/**
	 * Reculer l'heure de départ de gare de tempsAvance seconde(s)
	 * pour l'arrêt passé en paramètre
	 * @param a
	 * @param tempsAvance
	 */
	public void retardHeureDepartDeGare(Arret a, int tempsRetard) {
		LocalDateTime newHeureDepartDeGare = a.getHeureDepartDeGare().plusSeconds(tempsRetard);
		em.getTransaction().begin();
		a.setHeureDepartDeGare(newHeureDepartDeGare);
		em.getTransaction().commit();
	}

	public List<Arret> getAllArrets() {
		return (List<Arret>) em.createNamedQuery("Arret.getAllArrets", Arret.class).getResultList();
	}
}
