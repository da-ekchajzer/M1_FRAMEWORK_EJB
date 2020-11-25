package fr.pantheonsorbonne.ufr27.miage.n_dao;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;

@ManagedBean
public class ArretDAO {

	@Inject
	EntityManager em;

	@Inject
	ItineraireDAO itineraireDAO;

	public boolean removeArretFromItineraire(int idTrain, Arret arret) {
		// On récupère l'itinéraire associé au train
		Itineraire itineraire = itineraireDAO.getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);

		int nbArretsAvantSuppression = itineraire.getGaresDesservies().size();

		// On supprime dans la table ITINERAIRE_ARRET à l'aide du couple idItineraire - idArret
		em.getTransaction().begin();
		em.remove(arret);
		em.getTransaction().commit();
		
		return itineraire.getGaresDesservies().size() == nbArretsAvantSuppression - 1;
	}

}
