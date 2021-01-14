package fr.pantheonsorbonne.ufr27.miage.service;

public interface ServiceUtilisateur {

	/**
	 * Faire monter/descendre les voyageurs d'un train 
	 * en fonction des étapes de leur voyage
	 * @param idTrain
	 */
	public void majUtilisateursTrain(int idTrain);

	/**
	 * Mettre tous les voyageurs concernés dans l'itinéraire
	 * avant son départ
	 * @param idTrain
	 */
	public void initUtilisateursItineraire(int idTrain);

}
