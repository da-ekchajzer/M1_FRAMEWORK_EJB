package fr.pantheonsorbonne.ufr27.miage.service.impl;

import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.TrainSansResa;
import fr.pantheonsorbonne.ufr27.miage.jpa.Voyageur;
import fr.pantheonsorbonne.ufr27.miage.repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.TrainRepository;
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
	TrainRepository trainRepository;

	@Inject
	ArretRepository arretRepository;

	@Inject
	VoyageurRepository voyageurRepository;

	@Override
	public void decideRetard(Retard retard, boolean isRetard) {
		if (isRetard) {
			if (retard.getItineraire().getTrain() instanceof TrainSansResa) {
				serviceMajExecuteur.retarderItineraire(retard.getItineraire(), retard.getTempsDeRetard());
				return;
			}

			Queue<Retard> retards = new LinkedList<Retard>();
			retards.add(retard);
			Retard retardEnTraitement;

			while (!retards.isEmpty()) {
				retardEnTraitement = retards.poll();
				serviceMajExecuteur.retarderItineraire(retardEnTraitement.getItineraire(), retard.getTempsDeRetard());
				retards.addAll(getRetardsItineraireEnCorespondance(retardEnTraitement));
				factoriseRetard(retards);
			}
		} else {
			if (retard.getItineraire().getTrain() instanceof TrainSansResa) {
				serviceMajExecuteur.avancerItineraire(retard.getItineraire(), retard.getTempsDeRetard());
				return;
			}

			Queue<Retard> retards = new LinkedList<Retard>();
			retards.add(retard);
			Retard retardEnTraitement;

			while (!retards.isEmpty()) {
				retardEnTraitement = retards.poll();
				serviceMajExecuteur.avancerItineraire(retardEnTraitement.getItineraire(), retard.getTempsDeRetard());
				retards.addAll(getRetardsItineraireEnCorespondance(retardEnTraitement));
				factoriseRetard(retards);
			}
		}
	}

	@Override
	public Collection<Retard> getRetardsItineraireEnCorespondance(Retard retard) {
		Itineraire itineraire = retard.getItineraire();
		LocalTime tempsRetard = retard.getTempsDeRetard();
		int count;
		Collection<Retard> retards = new HashSet<Retard>();

		LocalTime conditionRetard = LocalTime.of(2, 0, 0);

		Collection<Arret> arretRestants = itineraireRepository.getAllNextArrets(itineraire);

		for (Itineraire i : itineraireRepository.getAllItinerairesAtLeastIn(conditionRetard)) {
			int n = 3;
			// L'itinéraire lié au retard sera contenu dans la liste des itinéraires partant
			// dans - de 2h
			if (!i.equals(itineraire)) {
				Arret1Loop: for (Arret a1 : itineraireRepository.getAllNextArrets(i)) {
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

	// Cette méthode va être un enfer...
	@Override
	public void affecterUnAutreTrainAuxArretsDeItineraire(Itineraire itineraire) {
		// Il faut un liste de tous les itinéraire candidats
		List<Itineraire> itCandidats = itineraireRepository.getAllItineraires();
		itCandidats.remove(itineraire);
		// ---
		// Si un itinéraire suit le même parcours mais en décalé et qu'il passe dans
		// moins de 2 heures
		for (Itineraire i : itCandidats) {
			// Si l'itinéraire i suit le même parcours que l'itinéraire en paramètre
			if (!i.isSameAs(itineraire)) {
				// On voit ce qu'on fait
				// Sinon
				// On récupère tous les autres itinéraires
				// Si un itinéraire passe à la même gare que celle de
				// itineraire.getArretActuel()
				// On voit ce qu'on fait
				// Fin si
			}
		}

	}

}
