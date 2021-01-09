package fr.pantheonsorbonne.ufr27.miage.n_service.impl;

import java.time.LocalTime;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.n_jms.MessageGateway;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceMajExecuteur;

@ManagedBean
@RequestScoped
public class ServiceMajExecuteurImp implements ServiceMajExecuteur {

	@Inject
	MessageGateway messageGateway;

	@Inject
	ItineraireRepository itineraireRepository;

	@Inject
	ArretRepository arretRepository;

	@Override
	public void ajouterArret(int idTrain, Arret arret) {
		itineraireRepository.ajouterUnArretEnCoursItineraire(idTrain, arret);
	}

	@Override
	public void supprimerArret(int idTrain, Arret arret) {
		// On avance l'heure d'arrivée à la gare d'arrêt qui suit l'Arret qui sera
		// supprimé
		avancerHeureArriveeEnGareArretSuivant(idTrain, arret);
		// On supprime l'arret de l'itinéraire de notre train
		itineraireRepository.supprimerArretDansUnItineraire(idTrain, arret);

		// TODO : Appeler JMS ArretPlusDesservi pour le train en question
	}

	private void avancerHeureArriveeEnGareArretSuivant(int idTrain, Arret arret) {
		// Récupérer l'arrêt qui suit celui qui va être supprimé de l'itinéraire de
		// notre train
		Arret nextArret = itineraireRepository.getNextArret(idTrain, arret);
		// Si l'arrêt supprimé n'est pas le dernier arrêt de l'itinéraire...
		if (nextArret != null) {
			int nbSecondes = 60;
			// On avance l'heure d'arrivée à la prochaine gare d'arrêt de nbSecondes
			arretRepository.avancerHeureArriveeEnGare(nextArret, nbSecondes);
		}
	}

	@Override
	public void retarderItineraire(Itineraire itineraire, LocalTime tempsRetard) {
		List<Arret> arretsSuivants = itineraireRepository.getAllNextArrets(itineraire);
		for (Arret a : arretsSuivants) {
			arretRepository.retarderHeureArriveeEnGare(a, tempsRetard.toSecondOfDay());
			arretRepository.retarderHeureDepartDeGare(a, tempsRetard.toSecondOfDay());
		}

		/*
		 * try { messageGateway.publishItineraire(itineraire, "majItineraire"); } catch
		 * (JAXBException | JMSException e) { e.printStackTrace(); }
		 */
	}

	@Override
	public void avancerItineraire(Itineraire itineraire, LocalTime tempsAvance) {
		List<Arret> arretsSuivants = itineraireRepository.getAllNextArrets(itineraire);
		for (Arret a : arretsSuivants) {
			arretRepository.avancerHeureArriveeEnGare(a, tempsAvance.toSecondOfDay());
			arretRepository.avancerHeureHeureDepartDeGare(a, tempsAvance.toSecondOfDay());
		}
	}

}
