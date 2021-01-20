package fr.pantheonsorbonne.ufr27.miage.service;

import java.time.LocalTime;

import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;

public interface ServiceMajExecuteur {

	/**
	 * Retarder l'itinéraire de tempsRetard heure(s)/minute(s)/seconde(s)
	 * 
	 * @param itineraire
	 * @param tempsRetard
	 */
	public void retarderItineraire(Itineraire itineraire, LocalTime tempsRetard);

	/**
	 * Avancer l'itinéraire de tempsAvance heure(s)/minute(s)/seconde(s)
	 * 
	 * @param itineraire
	 * @param tempsAvance
	 */
	public void avancerItineraire(Itineraire itineraire, LocalTime tempsAvance);

	/**
	 * Ajoute des arrêts au nouvelIt passé en paramètre basé sur les arrêts qui
	 * n'ont pas pu être desservis par l'ancienIt passé en paramètre
	 * 
	 * @param ancienIt
	 * @param nouvelIt
	 */
	public void transfererLesArretsSurItineraireDeSecours(Itineraire ancienIt, Itineraire nouvelIt);
}
