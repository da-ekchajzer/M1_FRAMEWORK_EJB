package fr.pantheonsorbonne.ufr27.miage.n_dao;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;

@ManagedBean
public class ItineraireDAO {

	@Inject
	EntityManager em;
	
	public Itineraire getItineraireEnCoursByEtatAndIdTrain(int idTrain, int etat) {
		return (Itineraire) em.createNativeQuery("SELECT * " +
		"FROM ITINERAIRE WHERE TRAIN_ID = ? AND ETAT = ?")
				.setParameter(1, idTrain)
				.setParameter(2, etat)
				.getSingleResult();
	}
	
	public void ajouterIncidentItineraire(int idItineraire, int idIncident) {
		em.createNativeQuery("UPDATE ITINERAIRE "
				+ "SET INCIDENT_ID = ? "
				+ "WHERE ID = ?")
					.setParameter(1, idIncident)
					.setParameter(2, idItineraire);
	}
}
