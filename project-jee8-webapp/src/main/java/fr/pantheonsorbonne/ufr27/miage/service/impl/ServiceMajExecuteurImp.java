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
	public void transfererArretsSurItineraireSecours(Itineraire ancienIt, Itineraire nouvelIt) {
		List<Arret> arretsToReproduce = itineraireRepository.getArretActuelAndAllNextArrets(ancienIt);
		arretsToReproduce.remove(ancienIt.getArretActuel());

		List<Arret> allnextArretsNouvelIt = itineraireRepository.getArretActuelAndAllNextArrets(nouvelIt);
		List<Arret> nextArretsNouvelIt = allnextArretsNouvelIt;
		Arret junctionArret = null;
		for (Arret a : allnextArretsNouvelIt) {
			allnextArretsNouvelIt.remove(a);
			if (a.getGare().equals(ancienIt.getArretActuel().getGare())) {
				junctionArret = a;
				break;
			}
		}

		// Les arrêts sont figés dans le temps donc seules les gares de ces arrêts sont
		// retenues pour créer de nouveaux arrêts à ces gares qu'il faut desservir
		List<Gare> garesToServe = new ArrayList<Gare>();
		ArretLoop: for (Arret a1 : arretsToReproduce) {
			garesToServe.add(a1.getGare());
			for (Arret a2 : nextArretsNouvelIt) {
				if (a2.getGare().equals(a1.getGare())) {
					garesToServe.remove(garesToServe.size() - 1);
					break ArretLoop;
				}
			}
		}
		// Pour éviter de coder le temps entre les arrêts et passé à quai d'une gare en
		// dur, le code ci-dessus calcule la moyenne des temps entre arrêts et à quais
		int averageTimeBetweenTwoArrets = 0, averageTimeOnThePlatform = 0, i = 0;
		for (Arret a : nouvelIt.getArretsDesservis()) {
			if (a.getHeureDepartDeGare() != null) {
				averageTimeBetweenTwoArrets += nouvelIt.getArretsDesservis().get(i + 1).getHeureArriveeEnGare()
						.toLocalTime().minusSeconds(a.getHeureDepartDeGare().toLocalTime().toSecondOfDay())
						.toSecondOfDay();
				if (a.getHeureArriveeEnGare() != null) {
					averageTimeOnThePlatform += a.getHeureDepartDeGare().toLocalTime()
							.minusSeconds(a.getHeureArriveeEnGare().toLocalTime().toSecondOfDay()).toSecondOfDay();
				}
			}
			i++;
		}
		averageTimeBetweenTwoArrets = averageTimeBetweenTwoArrets == 0 ? 0
				: averageTimeBetweenTwoArrets / (arretsToReproduce.size() - 1);
		averageTimeOnThePlatform = averageTimeOnThePlatform == 0 ? averageTimeBetweenTwoArrets / 2
				: averageTimeOnThePlatform / (arretsToReproduce.size() - 2);

		// Créer des arrêts cohérents à partir de la liste garesToServe
		LocalDateTime heureArrivee = junctionArret.getHeureDepartDeGare().plusSeconds(averageTimeBetweenTwoArrets);
		LocalDateTime heureDepart = heureArrivee.plusSeconds(averageTimeOnThePlatform);
		List<Arret> newArrets = new ArrayList<Arret>();
		newArrets.add(new Arret(garesToServe.get(0), heureArrivee, heureDepart));

		for (i = 1; i < garesToServe.size(); i++) {
			heureArrivee = newArrets.get(i - 1).getHeureDepartDeGare().plusSeconds(averageTimeBetweenTwoArrets);
			heureDepart = heureArrivee.plusSeconds(averageTimeOnThePlatform);
			newArrets.add(new Arret(garesToServe.get(i), heureArrivee, heureDepart));
		}

		// TODO
		// Ajouter les nouveaux arrêts à l'itineraire de secours chosedItineraire
		for (Arret a : nextArretsNouvelIt) {
			heureArrivee = a.getHeureArriveeEnGare().plusSeconds(averageTimeBetweenTwoArrets * garesToServe.size());
			a.setHeureArriveeEnGare(heureArrivee);
			if (a.getHeureDepartDeGare() != null) {
				heureDepart = heureArrivee.plusSeconds(averageTimeOnThePlatform);
				a.setHeureDepartDeGare(heureDepart);
			}
		}
		for (Arret a : newArrets) {
			itineraireRepository.ajouterUnArretDansItineraire(nouvelIt, a);
		}
	}

}
