package fr.pantheonsorbonne.ufr27.miage.n_repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.n_dao.VoyageurDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.VoyageurDAO.TrainSansResaNotExpectedException;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyage;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyageur;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;

@ManagedBean
@RequestScoped
public class VoyageurRepository {

	@Inject
	VoyageurDAO voyageurDAO;

	@Inject
	VoyageRepository voyageRepository;

	@Inject
	ItineraireRepository itineraireRepository;

	@Inject
	TrajetRepository trajetRepository;

	public List<Voyageur> getVoyageursByVoyageActuel(Voyage v) {
		return voyageurDAO.getVoyageursByVoyageActuel(v);
	}

	public void majVoyageursDansTrainAvecResa(Train train) {
		Itineraire itineraire = itineraireRepository.getItineraireByTrainEtEtat(train.getId(),
				CodeEtatItinieraire.EN_COURS);
		Set<Trajet> trajetsItineraire = new TreeSet<Trajet>(trajetRepository.getTrajetsByItineraire(itineraire));

		try {
			voyageurDAO.majVoyageursDansTrainAvecResa(train, itineraire, trajetsItineraire);
		} catch (TrainSansResaNotExpectedException e) {
			e.printStackTrace();
		}
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

	public boolean isVoyageurCorrespondance(Voyageur v, Itineraire itActuel, Itineraire itCorrespondance) {
		int count = 0, idxItActuel = 0, idxItCorresp = 0;
		for (Trajet t : v.getVoyageActuel().getTrajets()) {
			if (t.getItineraire().equals(itActuel)) {
				idxItActuel = ++count;
			} else if (t.getItineraire().equals(itCorrespondance)) {
				idxItCorresp = ++count;
			}
		}
		return (idxItCorresp > idxItActuel);
	}
}
