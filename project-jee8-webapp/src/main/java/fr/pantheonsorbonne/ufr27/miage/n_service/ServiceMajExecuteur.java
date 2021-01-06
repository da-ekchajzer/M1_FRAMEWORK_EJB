package fr.pantheonsorbonne.ufr27.miage.n_service;

import java.time.LocalTime;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;

public interface ServiceMajExecuteur {
	public void supprimerArret(int idTrain, Arret arret);

	public void ajouterArret(int idTrain, Arret Arret);

	void retarderItineraire(Itineraire itineraire, LocalTime tempsRetard);
}
