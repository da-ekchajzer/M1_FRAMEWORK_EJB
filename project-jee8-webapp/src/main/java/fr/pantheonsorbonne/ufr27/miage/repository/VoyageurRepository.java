package fr.pantheonsorbonne.ufr27.miage.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.dao.VoyageurDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.VoyageurDAO.TrainSansResaNotExpectedException;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.jpa.Voyage;
import fr.pantheonsorbonne.ufr27.miage.jpa.Voyageur;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire.CodeEtatItinieraire;

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

	/**
	 * Faire monter/descendre les voyageurs du train AVEC RESA passé en paramètre
	 * et de son itinéraire EN COURS associé en fonction des étapes de leur voyage 
	 * (gares de départ/d'arrivée/de correspondance)
	 * @param train
	 */
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

	/**
	 * Associer des voyageurs à l'itinéraire en cours du train d'id idTrain
	 * en fonction des étapes de leurs voyages
	 * @param idTrain
	 */
	public void mettreVoyageursDansItineraire(int idTrain) {
		// Récupérer l'itinéraire en cours associé au train
		Itineraire itineraire = itineraireRepository.getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);

		// Récupérer tous les trajets associés à cet itinéraire
		List<Trajet> trajets = trajetRepository.getTrajetsByItineraire(itineraire);

		// Récupérer les voyages qui contiennent au moins un des trajets de la liste
		List<Voyage> voyages = voyageRepository.getVoyagesComposedByAtLeastOneTrajetOf(trajets);

		// Ajouter tous les voyageurs concernés par au moins de ces voyages dans l'itinéraire
		List<Voyageur> voyageursToAdd = new ArrayList<Voyageur>();
		for (Voyage v : voyages) {
			voyageursToAdd.addAll(v.getVoyageurs());
		}
		voyageurDAO.mettreVoyageursDansItineraire(itineraire, voyageursToAdd);
	}

	/**
	 * Savoir si le voyageur v a bien l'itinéraire itCorrespondance de prévu 
	 * après son itinéraire actuel itActuel donc de savoir si c'est bien un 
	 * voyageur en correspondance
	 * @param v
	 * @param itActuel
	 * @param itCorrespondance
	 * @return
	 */
	public boolean isVoyageurCorrespondance(Voyageur v, Itineraire itActuel, Itineraire itCorrespondance) {
		int count = 0, idxItActuel = 0, idxItCorresp = 0;
		for (Trajet t : v.getVoyage().getTrajets()) {
			if (t.getItineraire().equals(itActuel)) {
				idxItActuel = ++count;
			} else if (t.getItineraire().equals(itCorrespondance)) {
				idxItCorresp = ++count;
			}
		}
		return (idxItCorresp > idxItActuel);
	}
	
	public List<Voyageur> getAllVoyageurs() {
		return voyageurDAO.getAllVoyageurs();
	}
}
