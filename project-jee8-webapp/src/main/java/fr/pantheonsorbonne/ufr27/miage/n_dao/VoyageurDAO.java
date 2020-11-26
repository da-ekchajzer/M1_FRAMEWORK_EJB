package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

	public void majVoyageursDansTrainAvecResa(int idTrain, Arret arret) {
		TrainAvecResa train = (TrainAvecResa) trainDAO.getTrainById(idTrain);

		Itineraire itineraire = itineraireDAO.getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);

		em.getTransaction().begin();
		// TODO

		for (Voyageur voyageur : itineraire.getVoyageurs()) {
			if (voyageur.getVoyage().getGareDeDepart().equals(arret.getGare())
					&& LocalDateTime.now().isBefore(arret.getHeureDepartDeGare())) {
				train.addVoyageurInTrain(voyageur);
				
				// TODO : removeUserItineraire ici ? (suppr les pers d'itineraire qd elles rentrent dans le train)
			}

			// TODO : Faut pas vérifier la gare d'arrivée des trajets d'un voyage plutôt ??
			if (voyageur.getVoyage().getGareArrivee().equals(arret.getGare())) {
				train.getVoyageurs().remove(voyageur);
			}
		}
		em.getTransaction().commit();
	}
	
	/**
	 * @author Mathieu
	 * 26/11/2020 (Matin)
	 * 
	 * Méthode permettant d'ajouter tous les voyageurs dans un Itinéraire au début de celui-ci 
	 * (Avant le départ du train)
	 * Pour cela, on cherche tous les voyages composés de l'itinéraire pour récupérer les voyageurs concernés
	 * 
	 * @param idTrain
	 */
	public void mettreVoyageursDansItineraire(int idTrain) {
		// Récupérer l'itinéraire en cours associé au train
		Itineraire itineraire = itineraireDAO.getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);

		// Récupérer tous les trajets associés à cet itinéraire
		List<Trajet> trajets = trajetDAO.getTrajetsByItineraire(itineraire);
		
		// Récupérer tous les voyages constitués d'un de ces trajets
		// Pour cela, on récupère tous les voyages puis on vérifie s'ils possèdent un des trajets
		List<Voyage> voyages = voyageDAO.getVoyagesComposesByUnTrajet(trajets);
		
		// On récupère l'ensemble des voyageurs à ajouter dans l'itinéraire
		List<Voyageur> voyageursToAdd = new ArrayList<Voyageur>();
		// TODO : Peut-on avoir des voyageurs présents dans plusieurs Voyages ?
		for(Voyage v : voyages) voyageursToAdd.addAll(v.getVoyageurs());
		
		// On ajoute les voyageurs dans l'itinéraire
		itineraire.setVoyageurs(voyageursToAdd);
	}
	
	

	
}
