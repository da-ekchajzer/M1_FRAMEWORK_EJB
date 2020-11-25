package fr.pantheonsorbonne.ufr27.miage.n_service.impl;

import java.sql.Time;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.IncidentJAXB;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceIncident;

public class ServiceIncidentImp implements ServiceIncident {

	@Override
	public boolean creerIncident(int idTrain, IncidentJAXB inc) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean majIncident(int idIncident, int etatIncident) {
		// TODO Auto-generated method stub
		return false;
		
	}
	
	//-----------------Modifs Sophia ----------------//

	@SuppressWarnings("unused")
	private void majEtatIncident(int idIncident, IncidentJAXB etat) {
		// TODO Auto-generated method stub
		//Recuperation du code etat incident et mise à jour
	}
	
	@SuppressWarnings("unused")
	private Time estimationTempsRetard(int idIncident) {
		// TODO Auto-generated method stub
		return null;
		//En fonction du code incident on a un temps de retard
	}
}
