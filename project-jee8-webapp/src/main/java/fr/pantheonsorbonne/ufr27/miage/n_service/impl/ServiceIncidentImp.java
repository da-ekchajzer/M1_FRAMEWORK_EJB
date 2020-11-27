package fr.pantheonsorbonne.ufr27.miage.n_service.impl;

import javax.inject.Inject;

import java.time.LocalTime;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.IncidentJAXB;
import fr.pantheonsorbonne.ufr27.miage.n_dao.IncidentDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident.CodeTypeIncident;
import fr.pantheonsorbonne.ufr27.miage.n_mapper.IncidentMapper;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceIncident;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceMajDecideur;

public class ServiceIncidentImp implements ServiceIncident {
	@Inject
	ServiceMajDecideur serviceMajDecideur;

	@Inject
	IncidentDAO incidentDAO;
	
	@Override
	public boolean creerIncident(int idTrain, IncidentJAXB inc) {
		Incident i = IncidentMapper.mapIncidentJAXBToIncident(inc);
		incidentDAO.creerIncident(idTrain, i);
		serviceMajDecideur.decideMajTrainCreation(idTrain, estimationTempsRetard(i.getTypeIncident()));
		
		return false;
	}

	@Override
	public boolean majEtatIncident(int idTrain, int etatIncident) {
		incidentDAO.updateEtatIncident(idTrain, etatIncident);
		serviceMajDecideur.decideMajTrainEnCours(idTrain, estimationTempsRetard(etatIncident));
		return false;
	}


	private LocalTime estimationTempsRetard(int codeTypeIncident) {
		return CodeTypeIncident.getTempEstimation(codeTypeIncident);
	}
}
