package fr.pantheonsorbonne.ufr27.miage.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;

public interface ServiceMajExecuteur {

	public void ajouterUnArretEnCoursItineraire(int idTrain, Arret Arret);

	public void ajouterUnArretEnBoutItineraire(int idTrain, Arret Arret, LocalDateTime heure);

	public void retarderItineraire(Itineraire itineraire, LocalTime tempsRetard);

	public void avancerItineraire(Itineraire itineraire, LocalTime tempsAvance);
}
