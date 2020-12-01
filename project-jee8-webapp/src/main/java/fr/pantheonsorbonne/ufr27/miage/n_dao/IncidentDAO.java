package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;

@ManagedBean
@RequestScoped
public class IncidentDAO {

	@Inject
	EntityManager em;

	public List<Incident> getAllIncidents() {
		TypedQuery<Incident> query = em.createNamedQuery("Incident.getAllIncidents", Incident.class);
		return query.getResultList();
	}

	public int getNbIncidents() {
		TypedQuery<Long> query = em.createNamedQuery("Incident.getNbIncidents", Long.class);
		return query.getSingleResult().intValue();
	}

	public Incident getIncidentById(int idIncident) {
		return em.createNamedQuery("Incident.getIncidentById", Incident.class).setParameter("id", idIncident)
				.getSingleResult();
	}

	public void ajouterIncidentEnBD(Incident incident) {
		// Persistence de l'incident
		em.getTransaction().begin();
		em.persist(incident);
		em.getTransaction().commit();
	}

	public void majEtatIncidentEnBD(Incident incident, int etat) {
		// MàJ de l'état de l'incident associé au train
		em.getTransaction().begin();
		incident.setEtat(etat);
		em.getTransaction().commit();
	}

	public void associerIncidentItineraire(Itineraire itineraire, Incident incident) {
		em.getTransaction().begin();
		itineraire.setIncident(incident);
		em.getTransaction().commit();
	}

}
