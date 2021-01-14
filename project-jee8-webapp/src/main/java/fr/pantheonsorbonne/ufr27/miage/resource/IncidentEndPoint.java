package fr.pantheonsorbonne.ufr27.miage.resource;

import java.time.temporal.ChronoUnit;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.IncidentJAXB;
import fr.pantheonsorbonne.ufr27.miage.repository.TrainRepository;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceIncident;

@Path("incident/")
public class IncidentEndPoint {

	@Inject
	ServiceIncident service;

	@Inject
	TrainRepository trainRepository;

	@Consumes(value = { MediaType.APPLICATION_XML })
	@Path("{trainId}")
	@POST
	public Response creerIncident(@PathParam("trainId") int trainId, IncidentJAXB incident) {
		System.out.println("== Infocentre - creerIncident ==\nidTrain : T" + trainId);
		int idTrain = trainRepository.getTrainByBusinessId(trainId).getId();
		if (service.creerIncident(idTrain, incident)) {
			return Response.ok().build();
		}
		return Response.serverError().build();
	}

	@Path("{trainId}/{etatIncident}")
	@PUT
	public Response majIncident(@PathParam("trainId") int trainId, @PathParam("etatIncident") int etatIncident) {
		System.out.println("== Infocentre - majIncident ==\nidTrain : T" + trainId);
		int idTrain = trainRepository.getTrainByBusinessId(trainId).getId();
		if (service.majEtatIncident(idTrain, etatIncident, 5, ChronoUnit.MINUTES)) {
			return Response.ok().build();
		}
		return Response.serverError().build();
	}
}