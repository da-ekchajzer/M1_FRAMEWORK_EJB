package fr.pantheonsorbonne.ufr27.miage.train;

import java.time.LocalDateTime;
import java.time.LocalTime;
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

	private final static int PERIODICITY = 5000;

	private int idTrain;
	private Random random;

	/**
	 * 0 - En attente; 1 - En cours; 2 - En incident
	 */
	private int etatTrain;

	// liste des arrêts de l'Itinéraire correspondant
	List<ArretTrain> arrets;
	int curentIdArret;
	LocalTime retardTotal;
	LocalTime retardActuel;
	LocalDateTime initialDepartureTime;
	LocalDateTime initialArrivalTime;
	LocalDateTime now;

	IncidentTrain incident;

	public Train(int idTrain) {
		random = new Random();
		this.idTrain = idTrain;
		this.etatTrain = 0;
		this.curentIdArret = 0;
		this.retardTotal = LocalTime.MIN;
		this.retardActuel = LocalTime.MIN;
		this.initialDepartureTime = null;
		this.initialArrivalTime = null;
		this.now = null;
	}

	@Override
	public void run() {
		while (etatTrain != -1) {
			actionTrain();

			try {
				Thread.sleep(PERIODICITY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void actionTrain() {
		now = LocalDateTime.now();

		switch (etatTrain) {

		case 0:
			if (updateItineraire(GatewayInfocentre.getItineraire(idTrain))) {
				curentIdArret = 0;
				etatTrain = 1;
				System.out.println("[ " + idTrain + " ] -- Debut de l'itineraire");
				System.out.println("[ " + idTrain + " ] >> arrets desservis : " + printArrets(arrets));
				GatewayInfocentre.sendCurrenArret(arrets.get(curentIdArret).getXMLArret(), idTrain);
				System.out.println("[ " + idTrain + " ] >> arret actuel : " + arrets.get(curentIdArret).getNomGare());
				initialDepartureTime = arrets.get(0).getHeureDepart();
				initialArrivalTime = arrets.get(arrets.size() - 1).getheureArrivee();
				System.out.println(
						"[ " + idTrain + " ] >> arrivee prevue au terminus : " + initialArrivalTime.toLocalTime());
				printRetardTotal();
				retardTotal = retardTotal.plusSeconds(retardActuel.toSecondOfDay());
			}
			break;

		case 1:
			if (now.isAfter(arrets.get(curentIdArret + 1).getheureArrivee())) {
				GatewayInfocentre.sendCurrenArret(arrets.get(++curentIdArret).getXMLArret(), idTrain);
				System.out.println("[ " + idTrain + " ] >> arret actuel : " + arrets.get(curentIdArret).getNomGare());
				printRetardTotal();
			} else {
				updateItineraire(GatewayInfocentre.getItineraire(idTrain));
			}
			if (arrets.get(curentIdArret).getHeureDepart() == null) {
				etatTrain = 0;
				System.out.println("[ " + idTrain + " ] -- Fin de l'itineraire");
				System.out.println("[ " + idTrain + " ] >> arrivee reelle au terminus : "
						+ arrets.get(curentIdArret).getheureArrivee().toLocalTime() + " ( " + now.toLocalTime() + " )");
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
				updateItineraire(GatewayInfocentre.getItineraire(idTrain));
				System.out.println("[ " + idTrain + " ] ** Fin de l'incident");
			} else {
				System.out.println("[ " + idTrain + " ] ** incident en cours");
			}
			break;

		default:
			break;
		}
	}

	private void genererRandomIncident() {
		boolean isIncident = false;

		if (now.isAfter(initialDepartureTime.plusSeconds(retardActuel.toSecondOfDay()))) {
			isIncident = bernouilliGenerator(0.5);
		} else {
			isIncident = bernouilliGenerator(0.1);
		}

		if (isIncident) {
			int typeIncident = random.nextInt(6) + 1;
			incident = new IncidentTrain(typeIncident);
			etatTrain = 2;
			IncidentJAXB incidentJAXB = incident.getXMLIncident();
			GatewayInfocentre.sendIncident(incidentJAXB, idTrain);
			System.out.println("[ " + idTrain + " ] ** Declaration d'un incident");
		} else if (now.isAfter(initialDepartureTime.plusSeconds(retardActuel.toSecondOfDay()))) {
			System.out.println("[ " + idTrain + " ] -- progression OK");
		}
	}

	private boolean bernouilliGenerator(double p) {
		if (random.nextDouble() < p) {
			return true;
		}
		return false;
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
		}
		if (initialArrivalTime != null && initialArrivalTime.isAfter(arrets.get(0).getHeureDepart())) {
			retardActuel = arrets.get(arrets.size() - 1).getheureArrivee().toLocalTime()
					.minusSeconds(initialArrivalTime.toLocalTime().toSecondOfDay());
		}
		return true;
	}

	private void printRetardTotal() {
		retardTotal = retardTotal.plusSeconds(retardActuel.toSecondOfDay());
		int h = retardTotal.getHour();
		int m = retardTotal.getMinute();
		int s = retardTotal.getSecond();
		String str = "";
		if (h == m && m == s && s == 0) {
			str += "le train est a l'heure";
		} else {
			if (h != 0) {
				str += h + " heure(s) ";
			}
			if (m != 0) {
				str += m + " minute(s) ";
			}
			if (s != 0) {
				str += s + " seconde(s)";
			}
		}
		System.out.println("[ " + idTrain + " ] $ retard total depuis le debut : " + str);
		retardTotal = retardTotal.minusSeconds(retardActuel.toSecondOfDay());
	}

	private String printArrets(List<ArretTrain> arrets) {
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
