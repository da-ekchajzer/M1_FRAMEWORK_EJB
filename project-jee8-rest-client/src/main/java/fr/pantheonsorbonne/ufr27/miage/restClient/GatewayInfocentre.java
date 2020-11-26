package fr.pantheonsorbonne.ufr27.miage.restClient;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ArretJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.IncidentJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ItineraireJAXB;

public class GatewayInfocentre {
	public static Client client = ClientBuilder.newClient();

	public static ItineraireJAXB getItineraire(int idTrain) {
		WebTarget target = client.target("http://localhost:8080");
		Response resp = target.path("itineraire").path("" + idTrain).request().get(Response.class);
		ItineraireJAXB itineraireJAXB = resp.readEntity(ItineraireJAXB.class);
		return itineraireJAXB;
	}

	public static void sendCurrenArret(ArretJAXB arretJAXB, int idTrain) {
		WebTarget target = client.target("http://localhost:8080");
		Response resp = target.path("itineraire").path("" + idTrain).request().accept(MediaType.APPLICATION_XML)
				.post(Entity.xml(arretJAXB));
	}

	public static void updateIncident(int etatIncident, int idTrain) {
		WebTarget target = client.target("http://localhost:8080");
		Response resp = target.path("incident").path("" + idTrain).path("" + etatIncident).request()
				.put(Entity.xml(null));
	}

	public static void sendIncident(IncidentJAXB incidentJAXB, int idTrain) {
		WebTarget target = client.target("http://localhost:8080");
		Response resp = target.path("incident").path("" + idTrain).request().accept(MediaType.APPLICATION_XML)
				.post(Entity.xml(incidentJAXB));
	}

}
