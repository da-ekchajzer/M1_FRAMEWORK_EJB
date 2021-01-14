package fr.pantheonsorbonne.ufr27.miage.service;

import java.util.Collection;
import java.util.Queue;

import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.service.utils.Retard;

public interface ServiceMajDecideur {

	/**
	 * Retarder/Avancer l'itinéraire associé au retard passé en paramètre
	 * puis, en cascade, les itinéraires en relation avec cet itinéraire initial
	 * @param retard
	 * @param isRetard
	 */
	public void decideRetard(Retard retard, boolean isRetard);

	/**
	 * Récupérer l'ensemble des itinéraires qui sont en relation avec l'itinéraire
	 * associé au retard passé en paramètre (suffisamment de voyageurs en correspondance, 
	 * départ dans moins de 2h et/ou gare(s) d'arrêt en commun)
	 * @param retard
	 * @return
	 */
	public Collection<Retard> getRetardsItineraireEnCorespondance(Retard retard);

	/**
	 * Factorise les différents retards subis par les itinéraires si besoin. Exemple :
	 * Si un itinéraire est retardé de 15min puis juste après de 10min, on ne va pas additionner
	 * les 2 retards mais plutôt ne pas prendre en compte le 2ème retard pour ne garder que les 15min
	 * de retard initiales
	 * @param retards
	 */
	public void factoriseRetard(Queue<Retard> retards);

	/**
	 * Associer à un autre train les arrêts qui doivent être desservi par l'itinéraire
	 * passé en paramètre lorsque son train est trop en retard
	 * @param itineraire
	 */
	public void affecterUnAutreTrainAuxArretsDeItineraire(Itineraire itineraire);
}