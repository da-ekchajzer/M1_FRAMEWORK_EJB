package fr.pantheonsorbonne.ufr27.miage.n_service.impl;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceMajDecideur;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceMajExecuteur;
import fr.pantheonsorbonne.ufr27.miage.n_service.utils.Retard;

@ManagedBean
@RequestScoped
public class ServiceMajDecideurImp implements ServiceMajDecideur {

	@Inject
	ServiceMajExecuteur serviceMajExecuteur;

	@Inject
	ItineraireRepository itineraireRepository;

	@Inject
	TrainRepository trainRepository;

	@Inject
	ArretRepository arretRepository;

	@Override
	public void decideRetard(Retard retard) {
		Queue<Retard> retards = new LinkedList<Retard>();
		retards.add(retard);
		Retard retardEnTraitement;

		while (!retards.isEmpty()) {
			retardEnTraitement = retards.poll();
			itineraireRepository.retarderItineraire(retardEnTraitement);
			retards.addAll(getRetardsItineraireEnCorespondance(retardEnTraitement.getItineraire()));
			factoriseRetard(retards);
		}
	}

	private Collection<Retard> getRetardsItineraireEnCorespondance(Itineraire itineraire) {
		// TODO
		Collection<Retard> retards = new HashSet<Retard>();
		// Iterer sur tous les itinéraires
		// conditionRetard = 2 heures pour retarder un train (règle métier)
		LocalTime conditionRetard = LocalTime.of(2, 0, 0);
		for (Itineraire i : itineraireRepository.getAllItinerairesAtLeastIn(conditionRetard)) {
			// TODO
		}
		// Si le train a un arret en commun
		// On regarde la règles de temps exemple : - de 10h avant correspondance
		// On regarde la règles de passager + 50
		return null;
	}

	private void factoriseRetard(Queue<Retard> retards) {
		Map<Itineraire, Retard> mapRetards = new HashMap<Itineraire, Retard>();

		for (Retard r : retards) {
			if (mapRetards.containsKey(r.getItineraire())) {
				if (mapRetards.get(r.getItineraire()).getTempsDeRetard().toSecondOfDay() < r.getTempsDeRetard()
						.toSecondOfDay()) {
					mapRetards.put(r.getItineraire(), r);
				}
			} else {
				mapRetards.put(r.getItineraire(), r);
			}
		}

		retards.clear();
		retards.addAll(mapRetards.values());
	}

	@Override
	public void decideMajTrainFin(int idTrain) {
		// TODO Auto-generated method stub

	}

}
