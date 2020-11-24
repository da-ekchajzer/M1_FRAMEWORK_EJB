package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TypedQuery;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;

@ManagedBean
@NamedQueries({
	@NamedQuery(name="IncidentDAO.findAll", query="SELECT i FROM Incident i"),
	@NamedQuery(name="IncidentDAO.getNbIncidents", query="SELECT COUNT(i) FROM Incident i"),
	@NamedQuery(name="IncidentDAO.getIncidentById", query="SELECT i FROM Incident i WHERE i.ID = ?")
})
public class IncidentDAO {

	@Inject
	EntityManager em;
	
	@Inject 
	ItineraireDAO itineraireDAO;
	
	public List<Incident> getAllIncidents() {
		TypedQuery<Incident> query =
			      em.createNamedQuery("IncidentDAO.findAll", Incident.class);
		return query.getResultList();
	}

	public int getNbIncidents() {
		TypedQuery<Long> query =
			      em.createNamedQuery("IncidentDAO.getNbIncidents", Long.class);
		return query.getSingleResult().intValue();
	}
	
	
	public boolean creerIncident(int idTrain, Incident incident) {		
		// On récupére le nb d'Incidents en BD avant l'insertion
		int nbIncidentsAvantAjout = this.getNbIncidents();
		
		// Creation de l'incident
		em.createNativeQuery("INSERT INTO INCIDENT(DUREE, ETAT, HEUREDEBUT, TYPEINCIDENT) "
				+ "VALUES(?, ?, ?, ?)")
				.setParameter(1, incident.getDuree())
				.setParameter(2, incident.getEtat())
				.setParameter(3, incident.getHeureDebut())
				.setParameter(4, incident.getTypeIncident());
		
		// On récupére le nb d'Incidents en BD après l'insertion
		int nbIncidentsApresAjout = this.getNbIncidents();
				
		// On vérifie que l'insertion a été effectuée
		if (nbIncidentsApresAjout != nbIncidentsAvantAjout+1) {
			// LOG.error
			return false;
		}
		
		// Récupération de l'itinéraire EN COURS (=1) de TRAIN_ID idTrain
		Itineraire itineraire = itineraireDAO.getItineraireEnCoursByEtatAndIdTrain(1, idTrain);
		
		// Ajout de l'INCIDENT_ID dans l'Itineraire associé au train
		itineraireDAO.ajouterIncidentItineraire(itineraire.getId(), incident.getId());
		
		return true;
		
	}
	
	
	public void updateEtatIncident(int idTrain, int etat) {
		// Récupération de l'itinéraire EN COURS (=1) de TRAIN_ID idTrain
		Itineraire itineraire = itineraireDAO.getItineraireEnCoursByEtatAndIdTrain(1, idTrain);
	
		// Récupération de l'incident associé à l'itinéraire itineraire
		Incident incident = em.createNamedQuery("IncidentDAO.getIncidentById", Incident.class)
				.setParameter(1, itineraire.getIncident())
				.getSingleResult();
		
		// MàJ de l'état de l'incident associé au train
		em.createNativeQuery("UPDATE INCIDENT "
				+ "SET ETAT = ? "
				+ "WHERE ID = ?")
					.setParameter(1, etat)
					.setParameter(2, incident.getId());
	}
}
