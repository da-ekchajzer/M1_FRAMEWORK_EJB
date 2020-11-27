package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyage;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyageur;

@ManagedBean
public class VoyageurDAO {

	@Inject
	EntityManager em;

	@Inject
	TrainDAO trainDAO;

	@Inject
	ItineraireDAO itineraireDAO;

	@Inject
	TrajetDAO trajetDAO;

	@Inject
	VoyageDAO voyageDAO;

	public List<Voyageur> getVoyageursByVoyage(Voyage v) {
		return (List<Voyageur>) em.createNamedQuery("Voyageur.getVoyageursByVoyage", Voyageur.class)
				.setParameter("id", v.getId()).getResultList();
	}

	public void majVoyageursDansTrainAvecResa(int idTrain) {
		TrainAvecResa train = (TrainAvecResa) trainDAO.getTrainById(idTrain);

		Itineraire itineraire = itineraireDAO.getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);
		Set<Trajet> trajetsItineraire = new TreeSet<Trajet>(trajetDAO.getTrajetsByItineraire(itineraire));
		Set<Trajet> trajetsVoyageur;
		Iterator<Trajet> it;
		Trajet t, nextTrajet = null;

		em.getTransaction().begin();
		// TODO

		for (Trajet trajet : trajetsItineraire) {
			if (itineraire.getArretActuel().getGare().equals(trajet.getGareDepart())) {
				nextTrajet = trajet;
				break;
			}
		}

		for (Voyageur voyageur : itineraire.getVoyageurs()) {
			trajetsVoyageur = new TreeSet<Trajet>(voyageur.getVoyage().getTrajets());
			it = trajetsVoyageur.iterator();

			while (it.hasNext()) {
				t = it.next();
				if (itineraire.getArretActuel().getGare().equals(t.getGareDepart()) && !t.equals(nextTrajet)) {
					train.getVoyageurs().remove(voyageur);
					itineraire.getVoyageurs().remove(voyageur);
				}
			}
			if (voyageur.getVoyage().getGareArrivee().equals(itineraire.getArretActuel().getGare())) {
				itineraire.getVoyageurs().remove(voyageur);
			}

		}
		
//		 TODO : Faut faire une boucle sur les gares de départ des trajets
//		if (voyageur.getVoyage().getGareDeDepart().equals(arret.getGare())
//				&& LocalDateTime.now().isBefore(arret.getHeureDepartDeGare())) {
//			train.addVoyageurInTrain(voyageur);
//		}

		// TODO : Faut pas vérifier la gare d'arrivée des trajets d'un voyage plutôt ??
		// - Oui il faut faire ça - tester sur la derniere gare de l'itineraire

		// TODO : Faut faire une boucle sur les gares d'arrivées des trajets
//			for (Trajet t : voyageur.getVoyage().getTrajets()) {
//				if (arret.getGare().equals(t.getGareArrivee())) {
//					train.getVoyageurs().remove(voyageur);
//				}
//			}

		em.getTransaction().commit();
	}

	public void mettreVoyageursDansItineraire(int idTrain) {
		// Récupérer l'itinéraire en cours associé au train
		Itineraire itineraire = itineraireDAO.getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);

		// Récupérer tous les trajets associés à cet itinéraire
		List<Trajet> trajets = trajetDAO.getTrajetsByItineraire(itineraire);

		// Récupérer tous les voyages constitués d'un de ces trajets
		// Pour cela, on récupère tous les voyages puis on vérifie s'ils possèdent un
		// des trajets
		List<Voyage> voyages = voyageDAO.getVoyagesComposedByAtLeastOneTrajetOf(trajets);

		// On récupère l'ensemble des voyageurs à ajouter dans l'itinéraire
		List<Voyageur> voyageursToAdd = new ArrayList<Voyageur>();

		for (Voyage v : voyages) {
			voyageursToAdd.addAll(v.getVoyageurs());
		}

		em.getTransaction().begin();

		// On ajoute les voyageurs dans l'itinéraire
		itineraire.setVoyageurs(voyageursToAdd);

		em.getTransaction().commit();
	}

}
