package fr.pantheonsorbonne.ufr27.miage.n_resource;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ArretJAXB;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceItineraire;



@Path("itineraire/")
public class InitineraireEndpoint {
	
	@Inject
	ServiceItineraire service;
	
	
	@Produces(value = {MediaType.APPLICATION_XML})
	@Path("{trainId}")
	@GET
	public Response getItineraire(@PathParam("trainId") int trainId) {
		if(service.ItineraireExist(trainId)) {
			return Response.ok(service.getInitineraire(trainId)).build();
		}
		return Response.ok(null).build();
		
	}
	
	@Consumes(value = {MediaType.APPLICATION_XML})
	@Path("{trainId}")
	@PUT
	public Response majArret(@PathParam("trainId") int trainId, ArretJAXB arretActuel) {
		if(service.majItineraire(trainId, arretActuel)) {
			return Response.noContent().build();
		}
		return Response.serverError().build();
	}
	
}
