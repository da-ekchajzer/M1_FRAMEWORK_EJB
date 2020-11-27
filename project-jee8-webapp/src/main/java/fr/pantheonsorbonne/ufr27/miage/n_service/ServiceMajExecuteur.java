package fr.pantheonsorbonne.ufr27.miage.n_service;

import java.time.LocalTime;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;

public interface ServiceMajExecuteur {
	public void supprimerArret(Train idTrain, int idArret);
	public void ajouterArret(Train idTrain, Arret Arret);
	void retarderTrain(Train train, LocalTime tempsRetard);
}
