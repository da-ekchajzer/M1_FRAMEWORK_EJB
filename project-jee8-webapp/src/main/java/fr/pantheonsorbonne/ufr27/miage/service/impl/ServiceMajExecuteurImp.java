package fr.pantheonsorbonne.ufr27.miage.service.impl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceMajExecuteur;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceMajInfoGare;

@ManagedBean
@RequestScoped
public class ServiceMajExecuteurImp implements ServiceMajExecuteur {

	@Inject
	ServiceMajInfoGare serviceMajInfoGare;

	@Inject
	ItineraireRepository itineraireRepository;

	@Inject
	ArretRepository arretRepository;

	@Override
	public void ajouterUnArretEnCoursItineraire(int idTrain, Arret arret) {
		itineraireRepository.ajouterUnArretEnCoursItineraire(idTrain, arret);
	}

	@Override
	public void ajouterUnArretEnBoutItineraire(int idTrain, Arret arret, LocalDateTime heure) {
		itineraireRepository.ajouterUnArretEnBoutItineraire(idTrain, arret, heure);
	}

	@Override
	public void retarderItineraire(Itineraire itineraire, LocalTime tempsRetard) {
		List<Arret> arretsSuivants = itineraireRepository.getAllNextArrets(itineraire);
		for (Arret a : arretsSuivants) {
			arretRepository.retarderHeureArriveeEnGare(a, tempsRetard.toSecondOfDay());
			arretRepository.retarderHeureDepartDeGare(a, tempsRetard.toSecondOfDay());
		}

		serviceMajInfoGare.majHoraireItineraire(itineraire);

	}

	@Override
	public void avancerItineraire(Itineraire itineraire, LocalTime tempsAvance) {
		List<Arret> arretsSuivants = itineraireRepository.getAllNextArrets(itineraire);
		for (Arret a : arretsSuivants) {
			arretRepository.avancerHeureArriveeEnGare(a, tempsAvance.toSecondOfDay());
			arretRepository.avancerHeureHeureDepartDeGare(a, tempsAvance.toSecondOfDay());
		}

		serviceMajInfoGare.majHoraireItineraire(itineraire);
	}

}
