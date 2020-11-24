package fr.pantheonsorbonne.ufr27.miage.n_service;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;

public interface ServiceMajExecuteur {
	public void supprimerArret(Train idTrain, int idArret);
	public void retarderTrain(Train idTrain, double tempsRetard);
	public void ajouterArret(Train idTrain, Arret Arret);
}
