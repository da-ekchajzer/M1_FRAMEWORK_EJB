package fr.pantheonsorbonne.ufr27.miage.n_service;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;

public interface ServiceUtilisateur {
	public void ajoutItineraireUtilisateurs(int idTrain);
	public void retireItineraireUtilisateurs(int idTrain);
	
	public void majUtilisateursTrain(int idTrain, Arret arret);

}
