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
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyageur;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.TrainRepository;
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

		// On vérifie que ce soit bien un train AVEC resa
		if (trainConcerne instanceof TrainAvecResa) {
			// On récupère les voyageurs présents dans le train
			List<Voyageur> voyageursDansLeTrain = itineraire.getVoyageurs();
			// On récupère tous les itinéraires qui passe par le prochain arrêt du train
			List<Itineraire> itinerairesConcernesParLaGare = this.itineraireRepository
					.getItinerairesEnCoursOuEnIncidentByGare(nextArret.getGare());
			
			int cpt = 0;
			// Pour chaque itineraire concerne par le retard ...
			for(Itineraire i : itinerairesConcernesParLaGare) {
				// On récupère le prochain arrêt du train associée à l'itinéraire i
				Arret nextArretI = this.itineraireRepository.getNextArret(i.getTrain().getId(), i.getArretActuel());
				
				// Si l'arret actuel ou le prochain arret du train associé à i est impacté par le retard du train courant ...
				if(i.getArretActuel().getHeureDepartDeGare().isBefore(nextArret.getHeureArriveeEnGare()) ||
						nextArretI.getHeureDepartDeGare().isBefore(nextArret.getHeureArriveeEnGare())) {
					cpt = 0;
					// Pour chaque voyageur dans le train associé à i ...
					for(Voyageur voyageur : voyageursDansLeTrain) {
						// TODO : MEMO - Penser à vérifier si les voyageurs sont dans la liste de l'itinéraire dès la résa
						
						// Si le voyageur du train courant va prendre le train associé à i ...
						if(i.getVoyageurs().contains(voyageur)) {
							cpt++;
						}
					}
					// S'il y a plus de 50 voyageurs qui prendront le train associé à i ...
					if(cpt >= 50) {
						// i est considéré comme un itinéraire impacté
						itinerairesImpactes.add(i);
					}
				}
			}
			
			// Pour chaque itineraire impacté ...
			for(Itineraire i : itinerairesImpactes) {
				// TODO : condition d'arrêt de la récursivité à faire
				
				// On refait tous les tests du dessus (récursivité) pour retarder 
				// les trains associés aux itinéraires impactés
				//this.decideMajRetardTrainLorsCreationIncident(i.getTrain().getId(), estimationTempsRetard);
			}
			
			
			
			// PREMIERE IDEE MATHIEU
			/*
			// On récupère le voyage de chaque voyageur
			for (Voyageur voyageur : voyageursDansLeTrain) {
				Voyage voyage = voyageur.getVoyage();
				// On récupère les trajets d'un voyage
				List<Trajet> trajetsVoyage = voyage.getTrajets();
				// S'il existe + d'1 trajet dans la liste, le voyageur a une correspondance
				if (trajetsVoyage.size() > 1) {
					// Pour chaque trajet du voyage d'un voyageur...
					for (int i = 0; i < trajetsVoyage.size(); i++) {
						// On s'assure que le train (l'itineraire) actuel ne soit pas le dernier de son
						// voyage
						if (i < trajetsVoyage.size() - 1) {
							if (trajetsVoyage.get(i).getItineraire().equals(itineraire)) {

							}
						}

					}

				} else {
					// Le voyageur n'a pas de correspondance, on ne le garde pas dans les
					// voyageursConcernes
				}
			}
			 */
		} else {
			// On a pas le nb de voyageurs dans un train sans resa
		}

		/*********
		 * R2 : Si EstTmpsRetard > 2h on cherche le 1er train qui peut desservir les
		 * gares de l'itineraire du train de base
		 *******/
		// TODO !!!

		
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
