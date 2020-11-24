package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;

@ManagedBean
public class ArretDAO {

	@Inject
	EntityManager em;

	@Inject
	ItineraireDAO itineraireDAO;

	public boolean removeArretFromItineraire(int idTrain, Arret arret) {
		// On récupère l'itinéraire associé au train
		int idItineraire = this.itineraireDAO.getItineraireByEtatAndIdTrain(idTrain, 1).getId();

		int nbArretsAvantSuppression = this.getNbArretsByItineraire(idItineraire);

		// On supprime dans la table ITINERAIRE_ARRET à l'aide du couple idItineraire - idArret
		em.getTransaction().begin();
		em.createNativeQuery("DELETE FROM ITINERAIRE_ARRET "
				+ "WHERE ITINERAIRE_ID = ? "
				+ "AND GARESDESSERVIES_ID = ?")
					.setParameter(1, idItineraire)
					.setParameter(2, arret.getId()).executeUpdate();
		em.getTransaction().commit();

		int nbArretsApresSuppression = this.getNbArretsByItineraire(idItineraire);
		return nbArretsApresSuppression == nbArretsAvantSuppression - 1;
	}

	public List<Arret> getAllArretsByItineraire(int idItineraire) {
		return em.createNamedQuery("Itineraire.getAllArretsByItineraire", Arret.class).setParameter("id", idItineraire)
				.getResultList();
	}

	public int getNbArretsByItineraire(int idItineraire) {
		return ((Long) em.createNamedQuery("Itineraire.getNbArretsByItineraire", Long.class)
				.setParameter("id", idItineraire).getSingleResult()).intValue();
	}

}
