package fr.pantheonsorbonne.ufr27.miage.service.impl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.TrainSansResa;
import fr.pantheonsorbonne.ufr27.miage.jpa.Voyageur;
import fr.pantheonsorbonne.ufr27.miage.repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.VoyageurRepository;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceMajDecideur;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceMajExecuteur;
import fr.pantheonsorbonne.ufr27.miage.service.utils.Retard;

@ManagedBean
@RequestScoped
public class ServiceMajDecideurImp implements ServiceMajDecideur {

	@Inject
	ServiceMajExecuteur serviceMajExecuteur;

	@Inject
	ItineraireRepository itineraireRepository;

	@Inject
	VoyageurRepository voyageurRepository;

	@Override
	public void decideRetard(Retard retard, boolean isRetard) {
		LocalTime tempsRetard = retard.getTempsDeRetard();
		Queue<Retard> retards = new LinkedList<Retard>();
		retards.add(retard);

		for (Itineraire it : itineraireRepository.getAllItinerairesByTrainEtEtat(
				retard.getItineraire().getTrain().getId(), CodeEtatItinieraire.EN_ATTENTE)) {
			retards.add(new Retard(it, tempsRetard));
		}

		Retard retardEnTraitement = null;

		while (!retards.isEmpty()) {
			retardEnTraitement = retards.poll();
			if (retardEnTraitement.getItineraire().getTrain() instanceof TrainSansResa) {
				if (isRetard) {
					serviceMajExecuteur.retarderItineraire(retardEnTraitement.getItineraire(), tempsRetard);
				} else {
					serviceMajExecuteur.avancerItineraire(retardEnTraitement.getItineraire(), tempsRetard);
				}
				continue;
			} else {
				if (isRetard) {
					serviceMajExecuteur.retarderItineraire(retardEnTraitement.getItineraire(), tempsRetard);
				} else {
					serviceMajExecuteur.avancerItineraire(retardEnTraitement.getItineraire(), tempsRetard);
				}
				retards.addAll(getRetardsItineraireEnCorespondance(retardEnTraitement));
				factoriseRetard(retards);
			}
		}
	}

	@Override
	public Set<Retard> getRetardsItineraireEnCorespondance(Retard retard) {
		Itineraire itineraire = retard.getItineraire();
		LocalTime tempsRetard = retard.getTempsDeRetard();
		int count = 0;
		Set<Retard> retards = new HashSet<Retard>();

		LocalTime conditionRetard = LocalTime.of(2, 0, 0);

		List<Arret> arretRestants = itineraireRepository.getArretActuelAndAllNextArrets(itineraire);

		for (Itineraire i : itineraireRepository.getAllItinerairesAtLeastIn(conditionRetard)) {
			int n = 3;
			// L'itinéraire lié au retard sera contenu dans la liste des itinéraires partant
			// dans - de 2h
			if (!i.equals(itineraire)) {
				Arret1Loop: for (Arret a1 : itineraireRepository.getArretActuelAndAllNextArrets(i)) {
					for (Arret a2 : arretRestants) {
						if (a1.getGare().equals(a2.getGare())) {
							count = 0;
							for (Voyageur v : itineraire.getVoyageurs()) {
								if (voyageurRepository.isVoyageurCorrespondance(v, itineraire, i)) {
									count++;
								}
							}
							// S'il y a plus de n voyageurs ont la même correspondance alors on retarde leur
							// prochain itinéraire pour qu'ils puissent avoir leur train
							if (count > n) {
								retards.add(new Retard(i, tempsRetard));
								break Arret1Loop;
							}
						}
					}
				}
			}
		}
		return retards;
	}

	@Override
	public void factoriseRetard(Queue<Retard> retards) {
		Map<Itineraire, Retard> mapRetards = new HashMap<Itineraire, Retard>();

		for (Retard r : retards) {
			if (mapRetards.containsKey(r.getItineraire())) {
				if (mapRetards.get(r.getItineraire()).getTempsDeRetard().toSecondOfDay() < r.getTempsDeRetard()
						.toSecondOfDay()) {
					mapRetards.remove(r.getItineraire());
					mapRetards.put(r.getItineraire(), r);
				}
			} else {
				mapRetards.put(r.getItineraire(), r);
			}
		}

		retards.clear();
		retards.addAll(mapRetards.values());
	}

	// TODO : à finir, cette méthode est bientôt achevée...
	@Override
	public void affecterUnAutreTrainAuxArretsDeItineraire(Itineraire itineraire) {
		List<Itineraire> allItineraires = itineraireRepository.getAllItineraires();
		allItineraires.remove(itineraire);
		allItineraires.removeAll(itineraireRepository.getAllItinerairesByEtat(CodeEtatItinieraire.FIN));
		LocalDateTime inTwoHours = LocalDateTime.now().plusSeconds(LocalTime.of(2, 0, 0, 0).toSecondOfDay());
		// Sélection des potentiels itinéraires de secours
		List<Itineraire> itinerairesCandidates = new ArrayList<Itineraire>();
		boolean isItInLessThanTwoHours = false;
		for (Itineraire it : allItineraires) {
			isItInLessThanTwoHours = it.getArretsDesservis().get(0).getHeureDepartDeGare().isBefore(inTwoHours);
			// Si l'itinéraire i passe par la même gare que celle de l'arrêt actuel de
			// l'itinéraire en paramètre et qu'il passe dans moins de 2 heures
			if (it.isGareDesservie(itineraire.getArretActuel().getGare()) && isItInLessThanTwoHours) {
				for (Arret a : itineraireRepository.getArretActuelAndAllNextArrets(it)) {
					if (a.getGare().equals(itineraire.getArretActuel().getGare())) {
						itinerairesCandidates.add(it);
					}
				}
			}
		}
		// Premier tri parmi les potentiels itinéraires de secours s'il y en a
		if (!itinerairesCandidates.isEmpty()) {
			List<Arret> arretsToReplace = itineraireRepository.getArretActuelAndAllNextArrets(itineraire);
			arretsToReplace.remove(itineraire.getArretActuel());
			List<Gare> garesToServe = new ArrayList<Gare>();
			for (Arret a : arretsToReplace) {
				garesToServe.add(a.getGare());
			}
			List<Itineraire> itBestCandidates = new ArrayList<Itineraire>();
			for (Itineraire it : itinerairesCandidates) {
				if (it.getEtat() == CodeEtatItinieraire.EN_COURS.getCode()) {
					itBestCandidates.add(it);
				}
			}
			if (itBestCandidates.isEmpty()) {
				for (Itineraire it : itinerairesCandidates) {
					if (it.getEtat() == CodeEtatItinieraire.EN_INCIDENT.getCode()) {
						itBestCandidates.add(it);
					}
				}
			}
			if (itBestCandidates.isEmpty()) {
				for (Itineraire it : itinerairesCandidates) {
					if (it.getEtat() == CodeEtatItinieraire.EN_ATTENTE.getCode()) {
						itBestCandidates.add(it);
					}
				}
			}
			itinerairesCandidates.clear();
			itinerairesCandidates.addAll(itBestCandidates);
			itBestCandidates.clear();
			for (Itineraire it : itinerairesCandidates) {
				if (it.getArretActuel().getGare().equals(itineraire.getArretActuel().getGare())) {
					itBestCandidates.add(it);
				}
			}
			if (itBestCandidates.isEmpty()) {
				itBestCandidates.addAll(itinerairesCandidates);
			}
			// Second tri parmi les potentiels itinéraires de secours
			Itineraire chosedItineraire = itBestCandidates.get(0);
			for (Itineraire it : itBestCandidates) {
				if (chosedItineraire.getArretActuel().isBefore(it.getArretActuel())) {
					chosedItineraire = it;
				}
			}
			// TODO
			// Créer des arrêts cohérents à partir de la liste garesToServe
			// Penser à vérifier les codes des méthodes ajouterUnArretEnCoursItineraire et
			// ajouterUnArretEnBoutItineraire, notamment dans la classe de DAO
			// Ajouter les nouveaux arrêts à l'itineraire de secours chosedItineraire
		}
	}

}
