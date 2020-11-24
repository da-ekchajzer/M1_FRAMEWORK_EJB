package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;

@ManagedBean
public class ItineraireDAO {

	@Inject
	EntityManager em;
	
	@Inject
	ArretDAO arretDao;
	
	public Itineraire getItineraireByEtatAndIdTrain(int idTrain, int etat) {				
		return em.createNamedQuery("Itineraire.getItineraireByTrainEtEtat", Itineraire.class)
				.setParameter("id", idTrain)
				.setParameter("etat", etat)
				.getSingleResult();
	}
	
	public void ajouterIncidentItineraire(int idItineraire, int idIncident) {
		em.createNativeQuery("UPDATE ITINERAIRE "
				+ "SET INCIDENT_ID = ? "
				+ "WHERE ID = ?")
					.setParameter(1, idIncident)
					.setParameter(2, idItineraire);
	}
	
	public boolean itineraireExiste(int idTrain) {
		@SuppressWarnings("unchecked")
		List<Itineraire> itineraires = (List<Itineraire>) em.createNativeQuery("SELECT * " +
				"FROM ITINERAIRE WHERE TRAIN_ID = ?")
						.setParameter(1, idTrain)
						.getSingleResult();
		
		if(itineraires.size() > 0) return true;
		else return false;
	}
	
	public void majEtatItineraire(int idItineraire, int newEtat) {
		em.createNativeQuery("UPDATE ITINERAIRE "
				+ "SET ETAT = ? "
				+ "WHERE ID = ?")
					.setParameter(1, newEtat)
					.setParameter(2, idItineraire);
	}
	
	public void updateArretActuel(int idTrain, Arret arret) {
		// TODO
	}
	
	
}
