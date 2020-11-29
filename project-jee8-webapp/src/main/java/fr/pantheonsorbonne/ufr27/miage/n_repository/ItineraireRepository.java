package fr.pantheonsorbonne.ufr27.miage.n_repository;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.n_dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.ItineraireDAO.MulitpleResultsNotExpectedException;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;

public class ItineraireRepository {

	@Inject
	ItineraireDAO itineraireDAO;
	
	@Inject
	IncidentRepository incidentRepository;
	
	@Inject
	TrajetRepository trajetRepository;
	
	public Itineraire recupItineraireEnCoursOuLeProchain(int idTrain) {
		Itineraire itineraire = null;
		try {
			itineraire = itineraireDAO.getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);
		} catch (MulitpleResultsNotExpectedException e) {
			e.printStackTrace();
		}

		if (itineraire == null) {
			try {
				itineraire = itineraireDAO.getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_INCIDENT);
			} catch (MulitpleResultsNotExpectedException e) {
				e.printStackTrace();
			}
		}

		if (itineraire == null) {
			List<Itineraire> itineraires = itineraireDAO.getAllItinerairesByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_ATTENTE);

			List<Arret> arrets = new ArrayList<Arret>();

			for (Itineraire i : itineraires) {
				arrets.add(i.getGaresDesservies().get(0));
			}

			itineraire = itineraires.get(0);

			for (int n = 0; n < itineraires.size(); n++) {
				if (arrets.get(n).getHeureDepartDeGare()
						.isBefore(itineraire.getGaresDesservies().get(0).getHeureDepartDeGare()))
					itineraire = itineraires.get(n);
			}
		}
		return itineraire;
	}
	
	public Itineraire getItineraireById(int idItineraire) {
		return itineraireDAO.getItineraireById(idItineraire);
	}

	public Itineraire getItineraireByTrainEtEtat(int idTrain, CodeEtatItinieraire etat) {
		try {
			return itineraireDAO.getItineraireByTrainEtEtat(idTrain, etat);
		} catch (MulitpleResultsNotExpectedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Itineraire> getAllItinerairesByTrainEtEtat(int idTrain, CodeEtatItinieraire etat) {
		return itineraireDAO.getAllItinerairesByTrainEtEtat(idTrain, etat);
	}

	public void ajouterIncidentItineraire(int idItineraire, int idIncident) {
		Itineraire itineraire = getItineraireById(idItineraire);
		Incident incident = incidentRepository.getIncidentById(idIncident);
		itineraireDAO.associerIncidentItineraire(itineraire, incident);
	}

	public void majEtatItineraire(int idItineraire, int newEtat) {
		Itineraire itineraire = getItineraireById(idItineraire);
		itineraireDAO.majEtatItineraire(itineraire, newEtat);
	}

	public void majArretActuel(int idTrain, Arret arret) {
		Itineraire itineraire = getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);
		itineraireDAO.majArretActuel(itineraire, arret);
	}

	public boolean supprimerArretDansUnItineraire(int idTrain, Arret arret) {
		// On récupère l'itinéraire associé au train
		Itineraire itineraire = getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);
		
		int nbArretsAvantSuppression = itineraire.getGaresDesservies().size();
		itineraireDAO.supprimerArretDansUnItineraire(itineraire, arret);
		return itineraire.getGaresDesservies().size() == nbArretsAvantSuppression - 1;
	}
	
	/*
	 * TODO : ajouter ce cas de figure dans le BDDFillerServiceImpl
	 */
	public boolean ajouterUnArretDansUnItineraire(int idTrain, Arret arret) {
		// On récupère l'itinéraire associé au train
		Itineraire itineraire = getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);
		
		Gare gare = arret.getGare();
		
		List<Trajet> trajets = trajetRepository.getTrajetsByItineraire(itineraire);

		int nbArretsAvantAjout = itineraire.getGaresDesservies().size();

		itineraireDAO.ajouterUnArretDansUnItineraire(itineraire, arret, gare, trajets);

		return itineraire.getGaresDesservies().size() == nbArretsAvantAjout + 1;
	}
	
	public void retarderTrain(int idTrain, LocalTime tempsRetard) {
		// On récupère l'itinéraire associé au train
		Itineraire itineraire = getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);

		Arret arretActuel = itineraire.getArretActuel();
		
		itineraireDAO.retarderTrain(tempsRetard, arretActuel, itineraire);
	}

}
