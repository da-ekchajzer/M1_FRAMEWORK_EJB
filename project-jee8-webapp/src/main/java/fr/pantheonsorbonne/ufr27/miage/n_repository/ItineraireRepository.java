package fr.pantheonsorbonne.ufr27.miage.n_repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.n_dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.ItineraireDAO.MulitpleResultsNotExpectedException;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;

@ManagedBean
@RequestScoped
public class ItineraireRepository {

	@Inject
	ItineraireDAO itineraireDAO;

	@Inject
	ArretRepository arretRepository;

	@Inject
	TrajetRepository trajetRepository;

	public Itineraire recupItineraireEnCoursOuLeProchain(int idTrain) {

		System.out.println("== recupItineraireEnCoursOuLeProchain ==");

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
			List<Itineraire> itineraires = itineraireDAO.getAllItinerairesByTrainEtEtat(idTrain,
					CodeEtatItinieraire.EN_ATTENTE);

			List<Arret> arrets = new ArrayList<Arret>();

			for (Itineraire i : itineraires) {
				arrets.add(i.getArretsDesservis().get(0));
			}

			itineraire = itineraires.get(0);

			for (int n = 0; n < itineraires.size(); n++) {
				if (arrets.get(n).getHeureDepartDeGare()
						.isBefore(itineraire.getArretsDesservis().get(0).getHeureDepartDeGare()))
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

	public void majEtatItineraire(Itineraire itineraire, CodeEtatItinieraire newEtat) {
		itineraireDAO.majEtatItineraire(itineraire, newEtat);
	}

	public void majArretActuel(int idTrain, Arret arret) {
		Itineraire itineraire = getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);
		itineraireDAO.majArretActuel(itineraire, arret);
	}

	public Itineraire supprimerArretDansUnItineraire(int idTrain, Arret arret) {
		Itineraire itineraire = getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);
		itineraire.getArretsDesservis().remove(arret);
		arretRepository.supprimerArret(arret);
		return itineraire;
	}

	/*
	 * TODO : ajouter ce cas de figure dans le BDDFillerServiceImpl
	 */
	public Itineraire ajouterUnArretEnCoursItineraire(int idTrain, Arret arret) {
		Itineraire itineraire = getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);
		itineraireDAO.ajouterUnArretEnCoursItineraire(itineraire, arret);
		return itineraire;
	}
	
	/**
	 * 
	 * @param idTrain
	 * @param arret
	 * @param heure heureDeDepart de l'ancienne gare d'arrivée ou heureArrivee de l'ancienne gare de départ
	 */
	public Itineraire ajouterUnArretEnBoutItineraire(int idTrain, Arret arret, LocalDateTime heure) {
		Itineraire itineraire = getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);
		itineraireDAO.ajouterUnArretEnBoutItineraire(itineraire, arret, heure);
		return itineraire;
	}
	
	public void retarderItineraireEnCoursOuEnIncident(Itineraire i, LocalTime tempsRetard) {
		Arret arretActuel = i.getArretActuel();
		itineraireDAO.retarderTrain(tempsRetard, arretActuel, i);
	}
	
	public void retarderItinerairesEnAttente(List<Itineraire> itineraires, LocalTime tempsRetard) {
		for (Itineraire i : itineraires) {
			Arret arretActuel = i.getArretActuel();
			itineraireDAO.retarderTrain(tempsRetard, arretActuel, i);
		}
	}

	public Arret getNextArret(int idTrain, Arret arret) {
		Itineraire itineraire = this.getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);
		
		// On est au dernier arrêt, y en a pas après
		if(arret.getHeureDepartDeGare() == null) return null;
		
		for (Arret a : itineraire.getArretsDesservis()) {
			if(a.getHeureArriveeEnGare() != null && a.getHeureArriveeEnGare().isAfter(arret.getHeureDepartDeGare())) {
				return a;
			}
		}
		return null;
	}

	// TODO : a finir !
	public Arret getNextArretByItineraireEtArretActuel(Itineraire itineraire, Arret arret) {
		for (Arret a : itineraire.getArretsDesservis()) {
			if (a.getHeureArriveeEnGare().isAfter(arret.getHeureDepartDeGare())) {
				return a;
			}
		}
		return null;
	}

	public List<Arret> getAllNextArrets(Itineraire itineraire, Arret arret) {
		List<Arret> arretsSuivants = new ArrayList<Arret>();
		// Si on est au dernier arrêt, y en a pas après donc on renvoie une liste vide
		if(arret.getHeureDepartDeGare() != null) {
			for (Arret a : itineraire.getArretsDesservis()) {
				if (a.getHeureArriveeEnGare() != null && a.getHeureArriveeEnGare().isAfter(arret.getHeureDepartDeGare())) {
					arretsSuivants.add(a);
				}
			}
		}
		return arretsSuivants;
	}

	public List<Itineraire> getItinerairesEnCoursOuEnIncidentByGare(Gare gare) {
		List<Itineraire> itinerairesConcernes = new ArrayList<Itineraire>();

		List<Itineraire> itinerairesEnCoursOuEnIncident = new ArrayList<Itineraire>();
		itinerairesEnCoursOuEnIncident.addAll(this.itineraireDAO.getAllItinerairesByEtat(CodeEtatItinieraire.EN_COURS));
		itinerairesEnCoursOuEnIncident
				.addAll(this.itineraireDAO.getAllItinerairesByEtat(CodeEtatItinieraire.EN_INCIDENT));

		for (Itineraire i : itinerairesEnCoursOuEnIncident) {
			for (Arret a : i.getArretsDesservis()) {
				if (a.getGare().equals(gare)) {
					itinerairesConcernes.add(i);
				}
			}
		}

		return itinerairesConcernes;
	}

	public List<Itineraire> getAllItinerairesByGare(Gare g) {
		List<Itineraire> itinerairesConcernes = new ArrayList<Itineraire>();
		List<Itineraire> allItineraires = this.itineraireDAO.getAllItineraires();
		for (Itineraire i : allItineraires) {
			for (Arret a : i.getArretsDesservis()) {
				if (a.getGare().equals(g)) {
					itinerairesConcernes.add(i);
				}
			}
		}
		return itinerairesConcernes;
	}

	public Itineraire getItineraireByBusinessId(String idItineraire) {
		// TODO Auto-generated method stub
		return null;
	}

}
