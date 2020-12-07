package fr.pantheonsorbonne.ufr27.miage.n_service.impl;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyage;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyageur;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.TrainRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.VoyageRepository;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceMajExecuteur;

@ManagedBean
@RequestScoped
public class ServiceMajDecideurImp implements fr.pantheonsorbonne.ufr27.miage.n_service.ServiceMajDecideur {

	@Inject
	ServiceMajExecuteur serviceMajExecuteur;

	@Inject
	ItineraireRepository itineraireRepository;

	@Inject
	TrainRepository trainRepository;
	
	@Inject
	ArretRepository arretRepository;

	/**
	 * Un TER n'attend pas un autre TER Un TGV n'attend pas un TER On ne gère pas
	 * l'arrêt des trains qui ont le même itinéraire à des heures différentes
	 * 
	 * 
	 */
	@Override
	public void decideMajRetardTrainLorsCreationIncident(int idTrain, LocalTime estimationTempsRetard) {
		// regarde le code etat, en deduis la duree de l'incident --> recup methode
		// estimationTempsRetard
		// Verifie avec les voyageurs si on ne doit pas supprimer, ajouter ou retarder
		// les arrets
		
		// Appel de l'exécuteur pour retarder le train d'id idTrain
		this.serviceMajExecuteur.retarderTrain(idTrain, estimationTempsRetard);
		
		// Récupérer l'ensemble des trains concernés par l'incident
		List<Train> trainsImpactes = new ArrayList<>();
		List<Itineraire> itinerairesImpactes = new ArrayList<>();
		
		/*********
		 * R1 : Si N personnes dans le train AVEC RESA doivent prendre une
		 * correspondance
		 *********/
		List<Voyageur> voyageursConcernesParIncident = new ArrayList<Voyageur>();
		Train trainConcerne = this.trainRepository.getTrainById(idTrain);
		Itineraire itineraire = this.itineraireRepository.getItineraireByTrainEtEtat(idTrain,
				CodeEtatItinieraire.EN_COURS);
		Arret nextArret = this.itineraireRepository.getNextArret(idTrain, itineraire.getArretActuel());

		this.deciderRetardRecursif(itineraire, nextArret, estimationTempsRetard);

		/*********
		 * R2 : Si EstTmpsRetard > 2h on cherche le 1er train qui peut desservir les
		 * gares de l'itineraire du train de base
		 *******/
		// TODO !!!

	}

	private void deciderRetardRecursif(Itineraire itineraireCourant, Arret a, LocalTime estimationTempsRetard) {
		// On vérifie que ce soit bien un train AVEC resa
		Train trainAssocieItineraireCourant = itineraireCourant.getTrain();
		if (trainAssocieItineraireCourant instanceof TrainAvecResa) {
			
			// Condition d'arret : Si a est le dernier Arret de l'Itineraire
			Arret nextArret = this.itineraireRepository.getNextArretByItineraireEtArretActuel(itineraireCourant, a);
			if(nextArret == null) return;
			
			// On récupère les itinéraires impactés par le retard d'itineraireCourant
			List<Itineraire> itinerairesImpactes = this.getItinerairesImpactes(a, itineraireCourant.getVoyageurs(), estimationTempsRetard);
			if(itinerairesImpactes.size() > 0) {
				// Pour chaque itinéraire impacté ...
				for(Itineraire itineraireImpacte : itinerairesImpactes) {
					// On le retarde
					this.serviceMajExecuteur.retarderTrain(itineraireImpacte.getTrain().getId(), estimationTempsRetard);
					// Puis on décide pour les arrêts suivants
					Arret nextArretItineraireImpacte = this.itineraireRepository.getNextArretByItineraireEtArretActuel(itineraireImpacte, itineraireImpacte.getArretActuel());
					this.deciderRetardRecursif(itineraireImpacte, nextArretItineraireImpacte, estimationTempsRetard);
				}
			}
			// On continue les décisions pour les prochains arrêts de l'itinéraire courant
			this.deciderRetardRecursif(itineraireCourant, nextArret, estimationTempsRetard);
			
		} else return;
	}
	
	/**
	 * Retourne les itinéraires qui passent par l'Arret a au même moment que l'itinéraire en cours
	 * (check sur les horaires + appel d'une méthode qui check le nb de voyageurs en correspondance)
	 * @param a
	 * @return
	 */
	private List<Itineraire> getItinerairesImpactes(Arret a, List<Voyageur> voyageursDansTrainCourant, LocalTime estimationTempsRetard) {
		List<Itineraire> itinerairesPossiblementImpactes = new ArrayList<Itineraire>();
		
		// On récupère tous les itinéraires qui passent, à un moment ou à un autre, par la Gare a.getGare()
		List<Itineraire> itinerairesPassantParA = this.itineraireRepository.getAllItinerairesByGare(a.getGare());
		// Pour chaque itinéraire passant par l'arrêt a ...
		for(Itineraire i : itinerairesPassantParA) {
			// On récupère l'Arret de l'itinéraire équivalent de a (même Gare)
			Arret arretConcerne = this.arretRepository.getArretParItineraireEtNomGare(i, a.getGare().getNom());
			// (On check les horaires) Si la correspondance n'est plus possible en raison du retard, 
			// alors l'itinéraire est possiblement impacté
			if(arretConcerne.getHeureDepartDeGare().isBefore(a.getHeureArriveeEnGare().plusSeconds(estimationTempsRetard.getSecond()))) {
				// S'il y a plus de 50 personnes dans le train actuel qui prendront le train associé à l'itinéraire i, 
				// alors i est un itinéraire impacté
				if(checkNbPersConcerneesParCorrespondance(voyageursDansTrainCourant, i)) {
					itinerairesPossiblementImpactes.add(i);
				}
			}
		}
		return itinerairesPossiblementImpactes;
	}
	
	/**
	 * Retourne vrai si plus de N (50) personnes du train actuel prendront l'itinéraire i comme correspondance
	 * @param voyageursDansTrainActuel
	 * @param i
	 * @return
	 */
	private boolean checkNbPersConcerneesParCorrespondance(List<Voyageur> voyageursDansTrainActuel, Itineraire i) {
		boolean enoughPers = false;
		// Pour chaque voyageur dans le train actuel ...
		for(Voyageur voyageur : voyageursDansTrainActuel) {
			// On récupère leur voyage
			Voyage voyage = voyageur.getVoyage();
			// On récupère les trajets du voyageur
			List<Trajet> trajets = voyage.getTrajets();
			// On récupère l'itinéraire de correspondance du voyageur
			// TODO : check si plusieurs itinéraires dans son voyage
			Itineraire itineraireDeCorrespondance = trajets.get(0).getItineraire();
			if(this.countNbPersDansItineraireDeCorrespondance(voyageursDansTrainActuel, itineraireDeCorrespondance) > 50) enoughPers = true;
		}
		return enoughPers;
	}
	
	/**
	 * Compte le nb de personnes dans le train actuel qui prendront le train associé à l'itinéraire passé en param
	 * @param voyageursDansTrainActuel
	 * @param itineraireDeCorrespondance
	 * @return
	 */
	private int countNbPersDansItineraireDeCorrespondance(List<Voyageur> voyageursDansTrainActuel, Itineraire itineraireDeCorrespondance) {
		int cpt = 0;
		for(Voyageur v1 : voyageursDansTrainActuel) {
			for(Voyageur v2 : itineraireDeCorrespondance.getVoyageurs()) {
				if(v1.equals(v2)) cpt++;
			}
		}
		return cpt;
	}
	
	@Override
	public void decideMajTrainEnCours(int idTrain, LocalTime estimationTempsRetardEnCours) {
		// TODO Auto-generated method stub

	}

	@Override
	public void decideMajTrainFin(int idTrain) {
		// TODO Auto-generated method stub

	}

}
