package fr.pantheonsorbonne.ufr27.miage.service;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ArretJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ItineraireJAXB;

public interface ServiceItineraire {

	public ItineraireJAXB getItineraire(int idTrain);

	public boolean majItineraire(int idTrain, ArretJAXB a);
}
