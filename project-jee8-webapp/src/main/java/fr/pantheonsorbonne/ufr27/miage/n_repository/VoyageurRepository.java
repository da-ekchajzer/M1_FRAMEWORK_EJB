package fr.pantheonsorbonne.ufr27.miage.n_repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.n_dao.VoyageurDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyage;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyageur;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;

public class VoyageurRepository {

	@Inject
	VoyageRepository voyageRepository;
	
	@Inject
	ItineraireRepository itineraireRepository;
	
	@Inject
	TrajetRepository trajetRepository;
	
	@Inject
	TrainRepository trainRepository;
	
	@Inject
	VoyageurDAO voyageurDAO;

	public List<Voyageur> getVoyageursByVoyage(Voyage v) {
		return voyageurDAO.getVoyageursByVoyage(v);
	}
	
	public void majVoyageursDansTrainAvecResa(int idTrain) {
		TrainAvecResa train = (TrainAvecResa) trainRepository.getTrainById(idTrain);

		Itineraire itineraire = itineraireRepository.getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);
		Set<Trajet> trajetsItineraire = new TreeSet<Trajet>(trajetRepository.getTrajetsByItineraire(itineraire));
		
		voyageurDAO.majVoyageursDansTrainAvecResa(train, itineraire, trajetsItineraire);
	}
	
	public void mettreVoyageursDansItineraire(int idTrain) {
		// Récupérer l'itinéraire en cours associé au train
		Itineraire itineraire = itineraireRepository.getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);

		// Récupérer tous les trajets associés à cet itinéraire
		List<Trajet> trajets = trajetRepository.getTrajetsByItineraire(itineraire);

		// Récupérer tous les voyages constitués d'un de ces trajets
		// Pour cela, on récupère tous les voyages puis on vérifie s'ils possèdent un
		// des trajets
		List<Voyage> voyages = voyageRepository.getVoyagesComposedByAtLeastOneTrajetOf(trajets);

		// On récupère l'ensemble des voyageurs à ajouter dans l'itinéraire
		List<Voyageur> voyageursToAdd = new ArrayList<Voyageur>();

		for (Voyage v : voyages) {
			voyageursToAdd.addAll(v.getVoyageurs());
		}
		voyageurDAO.mettreVoyageursDansItineraire(itineraire, voyageursToAdd);
	}
	
}
