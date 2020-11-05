package fr.pantheonsorbonne.ufr27.miage.n_resource;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.Trajet;


@Path("trajet/")
public class TrajetEndpoint {
	
	@Produces(value = {MediaType.APPLICATION_XML})
	@Path("{trainId}")
	@GET
	public Trajet getTrajet(@PathParam("trainId") int trainId) {
		return null;

	}
	
	@Path("{etatTrajet, trainId}")
	@PUT
	public void UpdateTrajetEtat(@PathParam("trainId") int trainId, @PathParam("etatTrajet") String etatTrajet) {

	}
	
}
