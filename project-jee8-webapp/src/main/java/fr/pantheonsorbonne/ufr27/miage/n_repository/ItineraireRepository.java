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
			if (itineraires.isEmpty()) {
				return null;
			}
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

	public Itineraire getItineraireByBusinessId(String businessIdItineraire) {
		return itineraireDAO.getItineraireByBusinessId(businessIdItineraire);
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

	public void majArretActuel(Itineraire itineraire, Arret arret) {
		if (itineraire.getNextArret().getGare().equals(arret.getGare())) {
			itineraireDAO.majArretActuel(itineraire, arret);
		}
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
	 * @param heure   heureDeDepart de l'ancienne gare d'arrivée ou heureArrivee de
	 *                l'ancienne gare de départ
	 */
	public Itineraire ajouterUnArretEnBoutItineraire(int idTrain, Arret arret, LocalDateTime heure) {
		Itineraire itineraire = getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);
		itineraireDAO.ajouterUnArretEnBoutItineraire(itineraire, arret, heure);
		return itineraire;
	}

	public Arret getNextArret(int idTrain, Arret arret) {
		Itineraire itineraire = this.getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);

		// On est au dernier arrêt, y en a pas après
		if (arret == null || arret.getHeureDepartDeGare() == null)
			return null;

		for (Arret a : itineraire.getArretsDesservis()) {
			if (a.getHeureArriveeEnGare() != null && a.getHeureArriveeEnGare().isAfter(arret.getHeureDepartDeGare())) {
				return a;
			}
		}
		return null;
	}

	public Arret getNextArretByItineraireEtArretActuel(Itineraire itineraire, Arret arret) {
		for (Arret a : itineraire.getArretsDesservis()) {
			if (a.isAfter(arret)) {
				return a;
			}
		}
		return null;
	}

	public List<Arret> getAllNextArrets(Itineraire itineraire) {
		List<Arret> arretsSuivants = new ArrayList<Arret>();
		Arret arretActuel = itineraire.getArretActuel();

		if (itineraire.getEtat() == CodeEtatItinieraire.EN_ATTENTE.getCode()) {
			arretsSuivants.addAll(itineraire.getArretsDesservis());
		} else if (itineraire.getEtat() == CodeEtatItinieraire.EN_COURS.getCode()
				|| itineraire.getEtat() == CodeEtatItinieraire.EN_INCIDENT.getCode()) {
			// Si on est au dernier arrêt, y en a pas après donc on renvoie une liste vide
			if (arretActuel != null && arretActuel.getHeureDepartDeGare() != null) {
				for (Arret a : itineraire.getArretsDesservis()) {
					if (a.getHeureArriveeEnGare() != null
							&& a.getHeureArriveeEnGare().isAfter(arretActuel.getHeureDepartDeGare())) {
						arretsSuivants.add(a);
					}
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

	public List<Itineraire> getAllItineraires() {
		return itineraireDAO.getAllItineraires();
	}

	public List<Itineraire> getAllItinerairesAtLeastIn(LocalTime duration) {
		List<Itineraire> itineraires = new ArrayList<Itineraire>();
		itineraires.addAll(itineraireDAO.getAllItinerairesByEtat(CodeEtatItinieraire.EN_COURS));
		itineraires.addAll(itineraireDAO.getAllItinerairesByEtat(CodeEtatItinieraire.EN_INCIDENT));
		System.out.println("****** " + itineraireDAO.getAllItinerairesByEtat(CodeEtatItinieraire.EN_ATTENTE).size());
		for (Itineraire i : itineraireDAO.getAllItinerairesByEtat(CodeEtatItinieraire.EN_ATTENTE)) {

			if (i.getArretsDesservis().get(0).getHeureDepartDeGare()
					.isBefore(LocalDateTime.now().plusHours(duration.getHour()).plusMinutes(duration.getMinute())
							.plusSeconds(duration.getSecond()))) {
				itineraires.add(i);
			}
		}
		return itineraires;
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

}
