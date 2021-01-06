package fr.pantheonsorbonne.ufr27.miage.n_service.impl;

import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyageur;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.TrainRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.VoyageurRepository;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceMajDecideur;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceMajExecuteur;
import fr.pantheonsorbonne.ufr27.miage.n_service.utils.Retard;

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
	public void decideRetard(Retard retard) {
		Queue<Retard> retards = new LinkedList<Retard>();
		retards.add(retard);
		Retard retardEnTraitement;

		while (!retards.isEmpty()) {
			retardEnTraitement = retards.poll();
			serviceMajExecuteur.retarderItineraire(retard.getItineraire(), retardEnTraitement.getTempsDeRetard());
			retards.addAll(getRetardsItineraireEnCorespondance(retardEnTraitement));
			factoriseRetard(retards);
		}
	}

	public Collection<Retard> getRetardsItineraireEnCorespondance(Retard retard) {
		Itineraire itineraire = retard.getItineraire();
		LocalTime tempsRetard = retard.getTempsDeRetard();
		int count;
		Collection<Retard> retards = new HashSet<Retard>();

		LocalTime conditionRetard = LocalTime.of(2, 0, 0);

		Collection<Arret> arretRestants = itineraireRepository.getAllNextArrets(itineraire);

		for (Itineraire i : itineraireRepository.getAllItinerairesAtLeastIn(conditionRetard)) {
			// L'itinéraire lié au retard sera contenu dans la liste des itinéraires partant dans - de 2h
			if(!i.equals(itineraire)) {
				Arret1Loop: for (Arret a1 : itineraireRepository.getAllNextArrets(i)) {
					for (Arret a2 : arretRestants) {
						if (a1.getGare().equals(a2.getGare())) {
							count = 0;
							for (Voyageur v : itineraire.getVoyageurs()) {
								if (voyageurRepository.voyageurHaveItineraire(v, i)) {
									count++;
								}
							}
							if (count > 5) {
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
	public void decideMajTrainFin(int idTrain) {
		// TODO Auto-generated method stub
	}

}
