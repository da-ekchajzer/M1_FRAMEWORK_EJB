package fr.pantheonsorbonne.ufr27.miage.n_resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("incident/")
public class IncidentEndPoint {

	@Consumes(value = {MediaType.APPLICATION_XML})
	@Path("{trainId}")
	@POST
	public Response createIncident(@PathParam("trainId") int trainId) {
		return null;

	}
	
	@Consumes(value = {MediaType.APPLICATION_XML})
	@Path("{trainId, etatIncident}")
	@PUT
	public Response UpdateIncident(@PathParam("trainId") int trainId) {
		return null;
	}
}
