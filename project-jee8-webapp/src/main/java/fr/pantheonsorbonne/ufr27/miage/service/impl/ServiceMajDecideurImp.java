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
		Set<Retard> retards = new HashSet<Retard>();
		int count = 0;

		LocalTime conditionRetard = LocalTime.of(2, 0, 0);

		List<Arret> arretRestants = itineraireRepository.getArretActuelAndAllNextArrets(itineraire);

		for (Itineraire i : itineraireRepository.getAllItinerairesAtLeastIn(conditionRetard)) {
			int n = 3;
			// L'itinéraire à l'origine du retard sera contenu dans la liste des itinéraires
			// partant dans - de 2h
			if (!i.equals(itineraire)) {
				ArretLoop: for (Arret a1 : itineraireRepository.getArretActuelAndAllNextArrets(i)) {
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
								break ArretLoop;
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

	@Override
	public Itineraire selectionnerUnItineraireDeSecours(Itineraire itineraire) {
		List<Itineraire> allItineraires = itineraireRepository.getAllItineraires();
		allItineraires.remove(itineraire);
		allItineraires.removeAll(itineraireRepository.getAllItinerairesByEtat(CodeEtatItinieraire.FIN));

		LocalDateTime inTwoHours = LocalDateTime.now().plusSeconds(LocalTime.of(2, 0, 0, 0).toSecondOfDay());

		Itineraire chosedItineraire = null;

		// Sélection des potentiels itinéraires de secours
		List<Itineraire> itinerairesOnTheSameLine = new ArrayList<Itineraire>();
		boolean isItInLessThanTwoHours = false;
		for (Itineraire it : allItineraires) {
			isItInLessThanTwoHours = it.getArretsDesservis().get(0).getHeureDepartDeGare().isBefore(inTwoHours);
			// Si l'itinéraire i passe par la même gare que celle de l'arrêt actuel de
			// l'itinéraire en paramètre et qu'il passe dans moins de 2 heures et l'arret
			// actuel de l'itinéraire accidenté ne doit pas être à la gare d'arrivée de l'it
			if (it.isGareDesservie(itineraire.getArretActuel().getGare()) && !it.getArretsDesservis()
					.get(it.getArretsDesservis().size() - 1).getGare().equals(itineraire.getArretActuel().getGare())
					&& isItInLessThanTwoHours) {
				for (Arret a : itineraireRepository.getArretActuelAndAllNextArrets(it)) {
					if (a.getGare().equals(itineraire.getArretActuel().getGare())) {
						itinerairesOnTheSameLine.add(it);
						break;
					}
				}
			}
		}
		allItineraires.clear();
		allItineraires.addAll(itinerairesOnTheSameLine);
		List<Arret> oldArretsItineraire = new ArrayList<Arret>();
		oldArretsItineraire.addAll(itineraire.getArretsDesservis());
		List<Arret> nextArretsItineraire = itineraireRepository.getArretActuelAndAllNextArrets(itineraire);
		oldArretsItineraire.removeAll(nextArretsItineraire);

		// Vérification que les itinéraires potentiels sélectionnés sont bien dans le
		// même sens que l'itinéraire accidenté
		List<Arret> oldArretsIt = new ArrayList<Arret>();
		List<Arret> nextArretsIt = new ArrayList<Arret>();
		for (Itineraire it : allItineraires) {
			oldArretsIt.addAll(it.getArretsDesservis());
			nextArretsIt = itineraireRepository.getArretActuelAndAllNextArrets(it);
			oldArretsIt.removeAll(nextArretsIt);
			ArretLoop: for (Arret a1 : oldArretsItineraire) {
				for (Arret a2 : nextArretsIt) {
					if (a1.getGare().equals(a2.getGare())) {
						itinerairesOnTheSameLine.remove(it);
						break ArretLoop;
					}
				}
			}
			if (itinerairesOnTheSameLine.contains(it)) {
				ArretLoop: for (Arret a1 : nextArretsItineraire) {
					for (Arret a2 : oldArretsIt) {
						if (a1.getGare().equals(a2.getGare())) {
							itinerairesOnTheSameLine.remove(it);
							break ArretLoop;
						}
					}
				}
			}
			// Paris - Dijon - Marseille -> ne fonctionne pas
			// Paris - Marseille - Montpellier -> fonctionne
			// Paris - Reims - Marseille -> accidenté
			oldArretsIt.clear();
			oldArretsIt.addAll(nextArretsIt);
			nextArretsIt.clear();
			List<Gare> nextGaresItineraire = new LinkedList<Gare>();
			for (Arret a : nextArretsItineraire) {
				nextGaresItineraire.add(a.getGare());
			}
			for (Arret a : oldArretsIt) {
				if (nextGaresItineraire.contains(a.getGare())) {
					nextArretsIt.add(a);
				} else {
					break;
				}
			}
			if (itinerairesOnTheSameLine.contains(it) && nextArretsIt.size() < 2) {
				itinerairesOnTheSameLine.remove(it);
			}
			oldArretsIt.clear();
			nextArretsIt.clear();
		}

		// Premier tri parmi les potentiels itinéraires de secours s'il y en a
		if (!itinerairesOnTheSameLine.isEmpty()) {
			// De préférence on réduit les itinéraires à seulement les itinéraires EN_COURS
			List<Itineraire> itBestCandidates = new ArrayList<Itineraire>();
			for (Itineraire it : itinerairesOnTheSameLine) {
				if (it.getEtat() == CodeEtatItinieraire.EN_COURS.getCode()) {
					itBestCandidates.add(it);
				}
			}
			// S'il n'y a pas d'itinéraires EN_COURS dans les itinéraires, on réduit les
			// itinéraires à seulement les itinéraires EN_ATTENTE
			if (itBestCandidates.isEmpty()) {
				for (Itineraire it : itinerairesOnTheSameLine) {
					if (it.getEtat() == CodeEtatItinieraire.EN_ATTENTE.getCode()) {
						itBestCandidates.add(it);
					}
				}
				// S'il n'y a pas d'itinéraires EN_ATTENTE non plus dans les itinéraires, on
				// réduit les itinéraires à seulement les itinéraires EN_INCIDENT
				if (itBestCandidates.isEmpty()) {
					for (Itineraire it : itinerairesOnTheSameLine) {
						if (it.getEtat() == CodeEtatItinieraire.EN_INCIDENT.getCode()) {
							itBestCandidates.add(it);
						}
					}
				}
			}

			// Second tri parmi les potentiels itinéraires de secours
			itinerairesOnTheSameLine.clear();
			itinerairesOnTheSameLine.addAll(itBestCandidates);
			itBestCandidates.clear();
			// De préférence on réduit les itinéraires à seulement ceux qui seront à la même
			// gare que l'itinéraire accidenté à leur prochain arrêt
			for (Itineraire it : itinerairesOnTheSameLine) {
				if (it.getNextArret() != null
						&& it.getNextArret().getGare().equals(itineraire.getArretActuel().getGare())) {
					itBestCandidates.add(it);
				}
			}
			// S'il n'y a pas d'itinéraire au même arrêt que l'itinéraire accidenté alors on
			// continue avec tous les itinéraires issus du premier tri
			if (itBestCandidates.isEmpty()) {
				itBestCandidates.addAll(itinerairesOnTheSameLine);
			}
			// Sélection de l'itinéraie de secours qui va desservir les nouveaux arrêts
			chosedItineraire = itBestCandidates.get(0);
			itBestCandidates.remove(chosedItineraire);
			Arret arretToCompare = null;
			for (Itineraire it : itBestCandidates) {
				// arretToCompare va stocker l'arrêt actuel de l'itinéraire accidenté
				for (Arret a : itineraireRepository.getArretActuelAndAllNextArrets(chosedItineraire)) {
					if (a.getGare().equals(itineraire.getArretActuel().getGare())) {
						arretToCompare = a;
						break;
					}
				}
				for (Arret a : itineraireRepository.getArretActuelAndAllNextArrets(it)) {
					if (a.getGare().equals(itineraire.getArretActuel().getGare())) {
						// Si l'itinéraire it arrive à l'arrêt actuel de l'itinéraire accidenté avant le
						// chosedItineraire alors l'itinéraie de secours chosedItineraire devient it
						if (a.isBefore(arretToCompare)) {
							chosedItineraire = it;
							break;
						}
					}
				}
			}
		}
		return chosedItineraire;
	}

}
