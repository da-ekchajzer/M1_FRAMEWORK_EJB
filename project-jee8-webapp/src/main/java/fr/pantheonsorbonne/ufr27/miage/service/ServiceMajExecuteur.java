package fr.pantheonsorbonne.ufr27.miage.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;

public interface ServiceMajExecuteur {

	/**
	 * Ajouter un arrêt au sein d'un itinéraire
	 * 
	 * @param idTrain
	 * @param Arret
	 */
	public void ajouterUnArretEnCoursItineraire(int idTrain, Arret Arret);

	/**
	 * Ajouter un arrêt à une extrémité d'un itinéraire (départ/terminus)
	 * 
	 * @param idTrain
	 * @param Arret
	 * @param heure
	 */
	public void ajouterUnArretEnBoutItineraire(int idTrain, Arret Arret, LocalDateTime heure);

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
}
