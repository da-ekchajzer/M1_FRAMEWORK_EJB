package fr.pantheonsorbonne.ufr27.miage.service;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ArretJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ItineraireJAXB;

public interface ServiceItineraire {

	/**
	 * Récupérer un ItineraireJAXB associé à l'itinéraire en cours ou
	 * le prochain itinéraire du train d'id idTrain
	 * @param idTrain
	 * @return
	 */
	public ItineraireJAXB getItineraire(int idTrain);

	/**
	 * Mettre à jour l'itinéraire ( = changer son arrêt actuel/faire avancer le train)
	 * grâce à un ArretJAXB reçu de l'API REST et passé en paramètre
	 * @param idTrain
	 * @param a
	 * @return
	 */
	public boolean majItineraire(int idTrain, ArretJAXB a);
}
