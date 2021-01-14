package fr.pantheonsorbonne.ufr27.miage.service;

import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;

public interface ServiceMajInfoGare {

	public void majHoraireTrain(int idTrain);
	
	public void majHoraireItineraire(Itineraire itineraire);

	void publishItineraire(Itineraire itineraire);
}
