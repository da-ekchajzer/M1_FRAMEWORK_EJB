package fr.pantheonsorbonne.ufr27.miage.n_service;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.IncidentJAXB;

public interface ServiceIncident {
	public boolean creerIncident(int idTrain, IncidentJAXB inc);
	public boolean majIncident(int idIncident,int etatIncident);
}
