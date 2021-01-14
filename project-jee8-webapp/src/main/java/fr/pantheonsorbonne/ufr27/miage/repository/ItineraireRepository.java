package fr.pantheonsorbonne.ufr27.miage.repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.ItineraireDAO.MulitpleResultsNotExpectedException;
import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire.CodeEtatItinieraire;

@ManagedBean
@RequestScoped
public class ItineraireRepository {

	@Inject
	ItineraireDAO itineraireDAO;

	@Inject
	ArretRepository arretRepository;

	@Inject
	TrajetRepository trajetRepository;

	/**
	 * Récupérer l'itinéraire actuel du train d'id idTrain ou, s'il n'y en a pas,
	 * son prochain itinéraire à parcourir (actuellement en attente)
	 * @param idTrain
	 * @return
	 */
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

	/**
	 * Récupérer l'itinéraire ayant comme id celui passé en paramètre
	 * @param idItineraire
	 * @return
	 */
	public Itineraire getItineraireById(int idItineraire) {
		return itineraireDAO.getItineraireById(idItineraire);
	}

	/**
	 * Récupérer l'itinéraire ayant comme businessId celui passé en paramètre
	 * @param businessIdItineraire
	 * @return
	 */
	public Itineraire getItineraireByBusinessId(String businessIdItineraire) {
		return itineraireDAO.getItineraireByBusinessId(businessIdItineraire);
	}

	/**
	 * Récupérer l'itinéraire associé au train d'id idTrain et
	 * ayant comme état celui passé en second paramètre
	 * @param idTrain
	 * @param etat
	 * @return
	 */
	public Itineraire getItineraireByTrainEtEtat(int idTrain, CodeEtatItinieraire etat) {
		try {
			return itineraireDAO.getItineraireByTrainEtEtat(idTrain, etat);
		} catch (MulitpleResultsNotExpectedException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Récupérer l'ensemble des itinéraires associés au train d'id idTrain et 
	 * ayant comme état celui passé en paramètre
	 * @param idTrain
	 * @param etat
	 * @return
	 */
	public List<Itineraire> getAllItinerairesByTrainEtEtat(int idTrain, CodeEtatItinieraire etat) {
		return itineraireDAO.getAllItinerairesByTrainEtEtat(idTrain, etat);
	}

	/**
	 * Changer l'état de l'itinéraire passé en paramètre
	 * 	- Le faire démarrer (EN_ATTENTE => EN_COURS),
	 * 	- Le mettre en incident (EN_COURS => EN_INCIDENT),
	 * 	- Le marquer comme terminé (EN_COURS => FIN),
	 * 	- etc...
	 * @param itineraire
	 * @param newEtat
	 */
	public void majEtatItineraire(Itineraire itineraire, CodeEtatItinieraire newEtat) {
		itineraireDAO.majEtatItineraire(itineraire, newEtat);
	}

	/**
	 * Mettre à jour l'arrêt actuel de l'itinéraire passé en paramètre
	 * => le faire passer à l'arrêt suivant
	 * @param itineraire
	 * @param arret
	 */
	public void majArretActuel(Itineraire itineraire, Arret arret) {
		itineraireDAO.majArretActuel(itineraire, arret);
	}

	/**
	 * Supprimer un arrêt de l'itinéraire EN COURS associé au train d'id idTrain
	 * @param idTrain
	 * @param arret
	 * @return
	 */
	public Itineraire supprimerArretDansUnItineraire(int idTrain, Arret arret) {
		Itineraire itineraire = getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);
		itineraire.getArretsDesservis().remove(arret);
		arretRepository.supprimerArret(arret);
		return itineraire;
	}

	/*
	 * TODO : ajouter ce cas de figure dans le BDDFillerServiceImpl ?
	 */
	/**
	 * Ajouter un arrêt au 'milieu' de l'itinéraire EN COURS associé au train d'id idTrain
	 * @param idTrain
	 * @param arret
	 * @return
	 */
	public Itineraire ajouterUnArretEnCoursItineraire(int idTrain, Arret arret) {
		Itineraire itineraire = getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);
		itineraireDAO.ajouterUnArretEnCoursItineraire(itineraire, arret);
		return itineraire;
	}

	/**
	 * Ajouter un arrêt comme départ ou terminus de l'itinéraire EN COURS 
	 * associé au train d'id idTrain
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

	/**
	 * Au sein de l'itinéraire passé en paramètre, 
	 * récupérer l'arrêt suivant l'arrêt passé en paramère
	 * @param itineraire
	 * @param arret
	 * @return
	 */
	public Arret getNextArretByItineraireEtUnArret(Itineraire itineraire, Arret arret) {
		for (Arret a : itineraire.getArretsDesservis()) {
			if (a.isAfter(arret)) {
				return a;
			}
		}
		return null;
	}

	/**
	 * Récupérer tous les arrêts après l'arrêt actuel de l'itinéraire passé en paramètre
	 * @param itineraire
	 * @return
	 */
	public List<Arret> getAllNextArrets(Itineraire itineraire) {
		List<Arret> arretsSuivants = new ArrayList<Arret>();
		Arret arretActuel = itineraire.getArretActuel();
		if (arretActuel == null) {
			System.err.println("*** arretActuel ne doit  pas être null pour pouvoir récupérer les arretsSuivants ***");
			return arretsSuivants;
		}

		if (itineraire.getEtat() == CodeEtatItinieraire.EN_ATTENTE.getCode()) {
			arretsSuivants.addAll(itineraire.getArretsDesservis());
		} else if (itineraire.getEtat() == CodeEtatItinieraire.EN_COURS.getCode()
				|| itineraire.getEtat() == CodeEtatItinieraire.EN_INCIDENT.getCode()) {
			arretsSuivants.add(arretActuel);
			// Si on est au dernier arrêt, y en a pas après donc on renvoie une liste vide
			if (arretActuel.getHeureDepartDeGare() != null) {
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

	/**
	 * Récupérer l'ensemble des itinéraires EN COURS OU EN INCIDENT qui passeront,
	 * à un moment ou à un autre, par la gare passée en paramètre
	 * @param gare
	 * @return
	 */
	public List<Itineraire> getItinerairesEnCoursOuEnIncidentByGare(Gare gare) {
		List<Itineraire> itinerairesConcernes = new ArrayList<Itineraire>();

		List<Itineraire> itinerairesEnCoursOuEnIncident = new ArrayList<Itineraire>();
		itinerairesEnCoursOuEnIncident.addAll(itineraireDAO.getAllItinerairesByEtat(CodeEtatItinieraire.EN_COURS));
		itinerairesEnCoursOuEnIncident.addAll(itineraireDAO.getAllItinerairesByEtat(CodeEtatItinieraire.EN_INCIDENT));

		for (Itineraire i : itinerairesEnCoursOuEnIncident) {
			for (Arret a : i.getArretsDesservis()) {
				if (a.getGare().equals(gare)) {
					itinerairesConcernes.add(i);
				}
			}
		}

		return itinerairesConcernes;
	}

	/**
	 * Récupérer l'ensemble des itinéraires en BD
	 * @return
	 */
	public List<Itineraire> getAllItineraires() {
		return itineraireDAO.getAllItineraires();
	}

	/**
	 * Récupérer les itinéraires en cours, en incident ou en attente avec un départ prévu dans moins de 
	 * 'duration' (passé en paramètre) heure(s)/minute(s)/etc..
	 * @param duration
	 * @return
	 */
	public List<Itineraire> getAllItinerairesAtLeastIn(LocalTime duration) {
		List<Itineraire> itineraires = new ArrayList<Itineraire>();
		itineraires.addAll(itineraireDAO.getAllItinerairesByEtat(CodeEtatItinieraire.EN_COURS));
		itineraires.addAll(itineraireDAO.getAllItinerairesByEtat(CodeEtatItinieraire.EN_INCIDENT));
		System.out.println("****** " + itineraireDAO.getAllItinerairesByEtat(CodeEtatItinieraire.EN_ATTENTE).size());
		for (Itineraire i : itineraireDAO.getAllItinerairesByEtat(CodeEtatItinieraire.EN_ATTENTE)) {

			if (i.getArretsDesservis().get(0).getHeureDepartDeGare()
					.isBefore(LocalDateTime.now().plusSeconds(duration.toSecondOfDay()))) {
				itineraires.add(i);
			}
		}
		return itineraires;
	}

	

}
