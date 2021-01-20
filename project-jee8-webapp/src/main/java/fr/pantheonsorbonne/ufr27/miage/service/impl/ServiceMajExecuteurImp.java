package fr.pantheonsorbonne.ufr27.miage.service.impl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
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
	public void retarderItineraire(Itineraire itineraire, LocalTime tempsRetard) {
		List<Arret> arretsSuivants = itineraireRepository.getArretActuelAndAllNextArrets(itineraire);
		for (Arret a : arretsSuivants) {
			arretRepository.retarderHeureArriveeEnGare(a, tempsRetard.toSecondOfDay());
			arretRepository.retarderHeureDepartDeGare(a, tempsRetard.toSecondOfDay());
		}
		serviceMajInfoGare.majHoraireItineraire(itineraire);
	}

	@Override
	public void avancerItineraire(Itineraire itineraire, LocalTime tempsAvance) {
		List<Arret> arretsSuivants = itineraireRepository.getArretActuelAndAllNextArrets(itineraire);
		for (Arret a : arretsSuivants) {
			arretRepository.avancerHeureArriveeEnGare(a, tempsAvance.toSecondOfDay());
			arretRepository.avancerHeureHeureDepartDeGare(a, tempsAvance.toSecondOfDay());
		}
		serviceMajInfoGare.majHoraireItineraire(itineraire);
	}

	@Override
	public void transfererLesArretsSurItineraireDeSecours(Itineraire ancienIt, Itineraire nouvelIt) {
		List<Arret> arretsToDuplicate = itineraireRepository.getArretActuelAndAllNextArrets(ancienIt);
		arretsToDuplicate.remove(ancienIt.getArretActuel());

		List<Arret> nextArretsNouvelIt = itineraireRepository.getArretActuelAndAllNextArrets(nouvelIt);
		Arret junctionArret = null;
		for (Arret a : nextArretsNouvelIt) {
			if (a.getGare().equals(ancienIt.getArretActuel().getGare())) {
				junctionArret = a;
				break;
			}
		}

		// Les arrêts sont figés dans le temps donc seules les gares de ces arrêts sont
		// retenues pour créer de nouveaux arrêts à ces gares qu'il faut desservir
		List<Gare> garesToServe = new ArrayList<Gare>();
		int averageTimeBetweenTwoArrets = 0, averageTimeOnThePlatform = 0;

		for (int i = 0; i < arretsToDuplicate.size(); i++) {
			garesToServe.add(arretsToDuplicate.get(i).getGare());
			for (Arret a : nextArretsNouvelIt) {
				if (a.getGare().equals(arretsToDuplicate.get(i).getGare())) {
					garesToServe.remove(garesToServe.size() - 1);
					junctionArret = a;
					break;
				}
			}
			if (i < arretsToDuplicate.size() - 1) {
				averageTimeBetweenTwoArrets += arretsToDuplicate.get(i + 1).getHeureArriveeEnGare().toLocalTime()
						.minusSeconds(arretsToDuplicate.get(i).getHeureDepartDeGare().toLocalTime().toSecondOfDay())
						.toSecondOfDay();
				if (arretsToDuplicate.get(i).getHeureArriveeEnGare() != null) {
					averageTimeOnThePlatform += arretsToDuplicate.get(i).getHeureDepartDeGare().toLocalTime()
							.minusSeconds(
									arretsToDuplicate.get(i).getHeureArriveeEnGare().toLocalTime().toSecondOfDay())
							.toSecondOfDay();
				}
			}
		}
		averageTimeBetweenTwoArrets /= (arretsToDuplicate.size() - 1);
		averageTimeOnThePlatform /= (arretsToDuplicate.size() - 1);

		// Créer des arrêts cohérents à partir de la liste garesToServe
		LocalDateTime heureArrivee = junctionArret.getHeureDepartDeGare() == null
				? junctionArret.getHeureArriveeEnGare()
						.plusSeconds(averageTimeOnThePlatform + averageTimeBetweenTwoArrets)
				: junctionArret.getHeureDepartDeGare().plusSeconds(averageTimeBetweenTwoArrets);
		List<Arret> newArrets = new ArrayList<Arret>();
		newArrets.add(new Arret(garesToServe.get(0), heureArrivee, heureArrivee.plusSeconds(averageTimeOnThePlatform)));

		for (int i = 1; i < garesToServe.size(); i++) {
			if (i < garesToServe.size() - 1) {
				heureArrivee = newArrets.get(i - 1).getHeureDepartDeGare().plusSeconds(averageTimeBetweenTwoArrets);
				newArrets.add(new Arret(garesToServe.get(i), heureArrivee,
						heureArrivee.plusSeconds(averageTimeOnThePlatform)));
			} else {
				heureArrivee = newArrets.get(i - 1).getHeureDepartDeGare().plusSeconds(averageTimeBetweenTwoArrets);
				newArrets.add(new Arret(garesToServe.get(i), heureArrivee, null));
			}
		}

		// TODO
		// Ajouter les nouveaux arrêts à l'itineraire de secours chosedItineraire
	}

}
