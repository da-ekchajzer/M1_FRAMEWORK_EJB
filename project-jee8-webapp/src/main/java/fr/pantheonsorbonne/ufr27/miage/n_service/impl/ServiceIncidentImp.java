package fr.pantheonsorbonne.ufr27.miage.n_service.impl;

import java.sql.Time;

import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.IncidentJAXB;
import fr.pantheonsorbonne.ufr27.miage.n_dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceIncident;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceMajDecideur;

public class ServiceIncidentImp implements ServiceIncident {
	@Inject
	ServiceMajDecideur serviceMajDecideur;
	

	
	@Override
	public boolean creerIncident(int idTrain, IncidentJAXB inc) {
		// TODO Auto-generated method stub
		Incident i = incidentFromXML(inc);
		//DAO
		serviceMajDecideur.decideMajTrainCreation(idTrain, estimationTempsRetard(i.getEtat()));
		
		return false;
	}

	@Override
	public boolean majEtatIncident(int idTrain, int etatIncident) {
		// TODO Recuperation du code etat incident et mise à jour
		//Get incident DAO from idTrain
		Incident i = new Incident();
		serviceMajDecideur.decideMajTrainEnCours(idTrain, estimationTempsRetardEnCours(i.getEtat()));
		return false;
		
	}
	

	private Time estimationTempsRetardEnCours(int etat) {
		// TODO Auto-generated method stub
		return null;
	}

	private Time estimationTempsRetard(int codeTypeIncident) {
		// TODO Auto-generated method stub
		//En fonction du code incident on a un temps de retard
		return null;

	}
	
	private Incident incidentFromXML(IncidentJAXB inc) {
		return null;
	}
}
