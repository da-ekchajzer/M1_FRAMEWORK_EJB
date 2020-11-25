package fr.pantheonsorbonne.ufr27.miage.n_service;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ArretJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.IncidentJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ItineraireJAXB;

public interface ServiceItineraire {
	public ItineraireJAXB getInitineraire(int idTrain);
	
	public boolean majItineraire(int idTrain, ArretJAXB a);
}
