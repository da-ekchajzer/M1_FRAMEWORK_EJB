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
				.setParameter("idTrain", idTrain).setParameter("etat", etat).getSingleResult();
	}

	public void ajouterIncidentItineraire(int idItineraire, int idIncident) {
		em.getTransaction().begin();
		em.createNativeQuery("UPDATE ITINERAIRE " + "SET INCIDENT_ID = ? " + "WHERE ID = ?").setParameter(1, idIncident)
				.setParameter(2, idItineraire).executeUpdate();
		em.getTransaction().commit();
	}

	public boolean itineraireExiste(int idTrain) {
		@SuppressWarnings("unchecked")
		List<Itineraire> itineraires = (List<Itineraire>) em
				.createNativeQuery("SELECT i " + "FROM ITINERAIRE i WHERE i.TRAIN_ID = ?").setParameter(1, idTrain)
				.getSingleResult();

		if (itineraires.size() > 0)
			return true;
		else
			return false;
	}

	public void majEtatItineraire(int idItineraire, int newEtat) {
		em.getTransaction().begin();
		em.createNativeQuery("UPDATE ITINERAIRE " + "SET ETAT = ? " + "WHERE ID = ?").setParameter(1, newEtat)
				.setParameter(2, idItineraire).executeUpdate();
		em.getTransaction().commit();
	}

	public void updateArretActuel(int idTrain, Arret arret) {
		em.getTransaction().begin();
		em.createNativeQuery("UPDATE ITINERAIRE " + "SET ARRETACTUEL_ID = ? " + "WHERE TRAIN_ID = ?")
				.setParameter(1, arret.getId()).setParameter(2, idTrain).executeUpdate();
		em.getTransaction().commit();
	}

}
