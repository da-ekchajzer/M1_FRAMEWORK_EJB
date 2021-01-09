package fr.pantheonsorbonne.ufr27.miage.main;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.xml.datatype.XMLGregorianCalendar;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ArretJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.IncidentJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ItineraireJAXB;
import fr.pantheonsorbonne.ufr27.miage.pojos.ArretTrain;
import fr.pantheonsorbonne.ufr27.miage.pojos.IncidentTrain;
import fr.pantheonsorbonne.ufr27.miage.restClient.GatewayInfocentre;

public class Train implements Runnable {

	private int idTrain;
	private Random random;

	/**
	 * 0 - En attente; 1 - En cours; 2 - En incident
	 */
	private int etatTrain;

	// liste des arrêts de l'Itinéraire correspondant
	List<ArretTrain> arrets;
	int curentIdArret;

	IncidentTrain incident;

	public Train(int idTrain) {
		random = new Random();
		this.idTrain = idTrain;
		this.etatTrain = 0;
		this.curentIdArret = 0;
	}

	@Override
	public void run() {
		while (etatTrain != -1) {
			actionTrain();

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

//		actionTrain();
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		incident = new IncidentTrain(4);
//		GatewayInfocentre.sendIncident(incident.getXMLIncident(), idTrain);
	}

	private void actionTrain() {
		switch (etatTrain) {

		case 0:

			if (updateItineraire(GatewayInfocentre.getItineraire(idTrain))) {
				curentIdArret = 0;
				etatTrain = 1;
				System.out.println("[ " + idTrain + " ] - Début de l'itineraire...");
				System.out.println("[ " + idTrain + " ] >> arrets desservis : " + printArrets(arrets));
				GatewayInfocentre.sendCurrenArret(arrets.get(curentIdArret).getXMLArret(), idTrain);
				System.out.println("[ " + idTrain + " ] >> arret actuel : " + arrets.get(curentIdArret).getNomGare());
			}
			break;

		case 1:

			if (LocalDateTime.now().isAfter(arrets.get(curentIdArret + 1).getheureArrivee())) {
				GatewayInfocentre.sendCurrenArret(arrets.get(++curentIdArret).getXMLArret(), idTrain);
				System.out.println("[ " + idTrain + " ] >> arret actuel : " + arrets.get(curentIdArret).getNomGare());
				updateItineraire(GatewayInfocentre.getItineraire(idTrain));
			}
			if (arrets.get(curentIdArret).getHeureDepart() == null) {
				etatTrain = 0;
				System.out.println("[ " + idTrain + " ] - ...fin de l'itineraire.");
			} else {
				genererRandomIncident();
			}
			break;

		case 2:
			if (solvedRandomIncident()) {
				incident.setEtatIncident(0);
				etatTrain = 1;
			}
			GatewayInfocentre.updateIncident(incident.getXMLIncident().getEtatIncident(), idTrain);
			if (etatTrain == 1) {
				System.out.println(
						"[ " + idTrain + " ] ***** ...fin de l'incident de type " + incident.getTypeIncident() + ".");
			} else {
				System.out.println("[ " + idTrain + " ] ***** L'incident de type " + incident.getTypeIncident()
						+ " est toujours en cours...");
			}
			break;

		default:
			break;
		}
	}

	private void genererRandomIncident() {
		boolean isIncident = bernouilliGenerator(0.5);

		if (isIncident) {
			int typeIncident = random.nextInt(6) + 1;
			incident = new IncidentTrain(typeIncident);
			etatTrain = 2;
			IncidentJAXB incidentJAXB = incident.getXMLIncident();
			GatewayInfocentre.sendIncident(incidentJAXB, idTrain);
			System.out.println(
					"[ " + idTrain + " ] ***** Début d'un incident de type " + incident.getTypeIncident() + "...");
		}
	}

	private boolean solvedRandomIncident() {
		boolean isFinIncident = bernouilliGenerator(0.5);
		return isFinIncident;
	}

	private boolean updateItineraire(ItineraireJAXB itineraireJAXB) {
		if (itineraireJAXB == null) {
			return false;
		}
		arrets = new LinkedList<ArretTrain>();
		List<ArretJAXB> arretsJAXB = itineraireJAXB.getArrets();

		for (int i = 0; i < arretsJAXB.size(); i++) {
			ArretJAXB arreti = arretsJAXB.get(i);
			arrets.add(new ArretTrain(arreti.getGare(), xmlGregorianCalendar2LocalDateTime(arreti.getHeureArrivee()),
					xmlGregorianCalendar2LocalDateTime(arreti.getHeureDepart())));
			System.out.println("[ " + idTrain + " ] - / " + arrets.get(i).toString());
		}
		return true;
	}

	private boolean bernouilliGenerator(double p) {
		if (random.nextDouble() < p) {
			return true;
		}
		return false;
	}

	public String printArrets(List<ArretTrain> arrets) {
		String str = "";
		for (ArretTrain arretTrain : arrets) {
			str += arretTrain.getNomGare() + " / ";
		}
		return str.substring(0, str.length() - 3);
	}

	public static LocalDateTime xmlGregorianCalendar2LocalDateTime(XMLGregorianCalendar xgc) {
		LocalDateTime ldt = null;
		if (xgc != null) {
			GregorianCalendar gc = xgc.toGregorianCalendar();
			ZonedDateTime zdt = gc.toZonedDateTime();
			ldt = zdt.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
		}
		return ldt;
	}

}
