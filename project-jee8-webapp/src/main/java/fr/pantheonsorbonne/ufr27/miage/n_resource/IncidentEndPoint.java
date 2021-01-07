package fr.pantheonsorbonne.ufr27.miage.n_resource;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.IncidentJAXB;
import fr.pantheonsorbonne.ufr27.miage.n_jms.ItineraireResponderBean;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceIncident;

@Path("incident/")
public class IncidentEndPoint {

	@Inject
	ServiceIncident service;

	@Consumes(value = { MediaType.APPLICATION_XML })
	@Path("{trainId}")
	@POST
	public Response creerIncident(@PathParam("trainId") int trainId, IncidentJAXB incident) {
		System.out.println("== Infocentre - creerIncident ==\nidTrain : "+ trainId);

		if (service.creerIncident(trainId, incident)) {
			return Response.noContent().build();
		}
		return Response.serverError().build();
	}

	@Path("{trainId}/{etatIncident}")
	@PUT
	public Response majIncident(@PathParam("trainId") int trainId, @PathParam("etatIncident") int etatIncident) {
		System.out.println("== Infocentre - majIncident ==\nidTrain : "+ trainId);
		if(service.majEtatIncident(trainId, etatIncident, 5)) {
			return Response.noContent().build();
		}
		return Response.serverError().build();
	}
}
