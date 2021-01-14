package fr.pantheonsorbonne.ufr27.miage.dao;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import fr.pantheonsorbonne.ufr27.miage.jpa.Incident;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Incident.CodeEtatIncident;

@ManagedBean
@RequestScoped
public class IncidentDAO {

	@Inject
	EntityManager em;

	/**
	 * Récupérer l'ensemble des incidents présents en BD
	 * @return
	 */
	public List<Incident> getAllIncidents() {
		TypedQuery<Incident> query = em.createNamedQuery("Incident.getAllIncidents", Incident.class);
		return query.getResultList();
	}

	/**
	 * Récupérer en BD l'incident ayant pour id idIncident
	 * @param idIncident
	 * @return
	 */
	public Incident getIncidentById(int idIncident) {
		return em.createNamedQuery("Incident.getIncidentById", Incident.class).setParameter("id", idIncident)
				.getSingleResult();
	}
	
	/**
	 * Récupérer en BD l'incident ayant pour businessId celui passé en paramètre
	 * @param businessId
	 * @return
	 */
	public Incident getIncidentByBusinessId(String businessId) {
		return em.createNamedQuery("Incident.getIncidentByBusinessId", Incident.class).setParameter("id", businessId)
				.getSingleResult();
	}

	/**
	 * Persister l'incident passé en paramètre en BD
	 * @param incident
	 */
	public void ajouterIncidentEnBD(Incident incident) {
		em.getTransaction().begin();
		em.persist(incident);
		em.getTransaction().commit();
	}

	/**
	 * Mettre à jour l'état de l'incident à l'état newEtat (= EN_COURS ou RESOLU)
	 * @param incident
	 * @param newEtat
	 */
	public void majEtatIncidentEnBD(Incident incident, CodeEtatIncident newEtat) {
		em.getTransaction().begin();
		incident.setEtat(newEtat.getCode());
		em.getTransaction().commit();
	}

	/**
	 * Avancer ou reculer l'heure de fin de l'incident en lui donnant la valeur
	 * newHeureDeFin passée en paramètre
	 * @param incident
	 * @param newHeureDeFin
	 */
	public void majHeureDeFinEnBD(Incident incident, LocalDateTime newHeureDeFin) {
		em.getTransaction().begin();
		incident.setHeureTheoriqueDeFin(newHeureDeFin);
		em.getTransaction().commit();
	}

	/**
	 * Associer en BD un incident et un itinéraire
	 * @param itineraire
	 * @param incident
	 */
	public void associerIncidentItineraire(Itineraire itineraire, Incident incident) {
		em.getTransaction().begin();
		itineraire.setIncident(incident);
		em.getTransaction().commit();
	}

}
