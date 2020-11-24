package fr.pantheonsorbonne.ufr27.miage.main;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.IncidentJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ObjectFactory;
import fr.pantheonsorbonne.ufr27.miage.pojos.ArretTrain;

public class Train implements Runnable {

	private int idTrain;
	private Random random;

	/**
	 * 0 - En attente 1 - En cours 2 - En incident
	 */
	private int etatTrain;
	List<ArretTrain> arrets;
	int curentIdArret;
	int currentIncidentEtat;

	public Train(int idTrain) {
		random = new Random();
		this.idTrain = idTrain;
		this.etatTrain = 0;
		this.curentIdArret = 0;
		this.currentIncidentEtat = 0;
	}

	@Override
	public void run() {
		while (etatTrain != -1) {

			actionTrain();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private void actionTrain() {
		switch (etatTrain) {

		case 0:
			if (getItineraire()) {
				curentIdArret = 0;
				etatTrain = 1;
			}
			break;

		case 1:
			getItineraire();

			if (arrets.get(curentIdArret + 1).getheureArrive().isBefore(LocalDateTime.now())) {
				sendCurrenArret();
				curentIdArret++;
			}
			if (arrets.get(curentIdArret).getHeureDepart() == null) {
				etatTrain = 0;
			}
			
			genererRandomIncident();
			break;

		case 2:
			if(solvedRandomIncident()) {
				updateIncident();
				currentIncidentEtat = 0;
				etatTrain = 1;
			}
			break;
		}
	}

	
	private void sendCurrenArret() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8080");

		IncidentJAXB incident = getNewIncident();
		System.out.println(incident.toString());
		Response resp = target.path("itineraire").request().accept(MediaType.APPLICATION_XML).post(Entity.xml(incident));
	}

	private boolean getItineraire() {
		return true;
	}
	

	private void sendIncident() {

	}
	
	private void updateIncident() {

	}
	
	
	
	private void genererRandomIncident() {
		boolean isIncident = false;
		//TODO generer true or false
		if(isIncident) {
			sendIncident();
			etatTrain = 2;
		}
	}

	private boolean solvedRandomIncident() {
		//TODO generer true or false
		return false;
	}
	

	private IncidentJAXB getNewIncident() {
		ObjectFactory factory = new ObjectFactory();
		IncidentJAXB incident = factory.createIncidentJAXB();

		try {
			incident.setTypeIncident(random.nextInt(4));
			incident.setEtatIncident(1);
			incident.setHeureIncident(
					DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDateTime.now().toString()));
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		return incident;
	}



}
