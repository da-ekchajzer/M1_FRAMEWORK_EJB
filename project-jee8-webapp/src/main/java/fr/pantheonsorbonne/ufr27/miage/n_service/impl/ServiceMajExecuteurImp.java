package fr.pantheonsorbonne.ufr27.miage.n_service.impl;

import java.time.LocalTime;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceMajExecuteur;

@ManagedBean
@RequestScoped
public class ServiceMajExecuteurImp implements ServiceMajExecuteur {
	
	@Inject
	ItineraireRepository itineraireRepository;
	
	@Inject
	ArretRepository arretRepository;
	
	@Override
	public void supprimerArret(int idTrain, Arret arret) {
		// On avance l'heure d'arrivée à la gare d'arrêt qui suit l'Arret qui va etre supprimé
		this.changementHoraireArriveeEnGareArretSuivant(idTrain, arret);
		// On supprime l'arret de l'itinéraire de notre train
		this.itineraireRepository.supprimerArretDansUnItineraire(idTrain, arret);
		
		// TODO : Appeler JMS ArretPlusDesservi pour le train en question
	}
	
	private void changementHoraireArriveeEnGareArretSuivant(int idTrain, Arret arret) {	
		// Récupérer l'arrêt qui suit celui qui va être supprimé de l'itinéraire de notre train
		Arret nextArret = this.itineraireRepository.getNextArret(idTrain, arret);
		// Si l'arrêt supprimé n'est pas le dernier arrêt de l'itinéraire...
		if(nextArret != null) {
			int nbSecondesAvance = 60;
			// On avance l'heure d'arrivée à la prochaine gare d'arrêt de nbSecondesAvance
			this.arretRepository.avancerHeureArriveeEnGare(nextArret, nbSecondesAvance);
		}
	}
	
	@Override
	public void retarderTrain(int idTrain, LocalTime tempsRetard) {
		Itineraire itineraire = this.itineraireRepository.recupItineraireEnCoursOuLeProchain(idTrain);
		
		// Retarde le train en param
		List<Arret> arretsSuivants = this.itineraireRepository.getAllNextArrets(itineraire, itineraire.getArretActuel());
		for(Arret a : arretsSuivants) {
			this.arretRepository.retarderHeureArriveeEnGare(a, tempsRetard.toSecondOfDay());
			this.arretRepository.retardHeureDepartDeGare(a, tempsRetard.toSecondOfDay());
			
			// TODO : Appeler JMS MajHeureArriveeTrain INFOGARE
		}
	}

	@Override
	public void ajouterArret(int idTrain, Arret arret) {
		itineraireRepository.ajouterUnArretEnCoursItineraire(idTrain, arret);
	}

}
