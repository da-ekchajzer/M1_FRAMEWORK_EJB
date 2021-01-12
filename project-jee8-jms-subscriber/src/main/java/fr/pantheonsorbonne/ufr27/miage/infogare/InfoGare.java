package fr.pantheonsorbonne.ufr27.miage.infogare;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.jms.JMSException;
import javax.xml.bind.JAXBException;

import fr.pantheonsorbonne.ufr27.miage.pojos.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jms.InfoGareProcessorBean;

public class InfoGare implements Runnable {

	String gare;
	InfoGareProcessorBean processor;
	Map<String, Itineraire> itineraires;

	public InfoGare(String gare) {
		this.gare = gare;
		this.itineraires = new HashMap<String, Itineraire>();

		SeContainerInitializer initializer = SeContainerInitializer.newInstance();

		try (SeContainer container = initializer.disableDiscovery().addPackages(true, InfoGareProcessorBean.class)
				.initialize()) {
			processor = container.select(InfoGareProcessorBean.class).get();
			processor.setInfoGare(this);
		}
	}

	@Override
	public void run() {

		while (true) {
			try {
				processor.consume();
				update();
				affichage();
			} catch (JMSException | JAXBException e) {
				e.printStackTrace();
			}
		}
	}

	private void update() {
		for (String s : itineraires.keySet()) {
			if (itineraires.get(s).getHeureArriveeEnGare() == null) {
				if (itineraires.get(s).getHeureDepartDeGare().isBefore(LocalDateTime.now())) {
					itineraires.remove(s);
				}
			} else if (itineraires.get(s).getHeureDepartDeGare() == null) {
				if (itineraires.get(s).getHeureArriveeEnGare().isBefore(LocalDateTime.now())) {
					itineraires.remove(s);
				}
			} else {
				if (itineraires.get(s).getHeureDepartDeGare().isBefore(LocalDateTime.now())) {
					itineraires.remove(s);
				}
			}
		}
	}

	private void affichage() {
		StringBuilder sb = new StringBuilder();
		sb.append("=== " + this.gare + " ===");
		sb.append("\n- Arrivées -");

		for (String s : itineraires.keySet()) {
			if (itineraires.get(s).getHeureArriveeEnGare() != null) {

				sb.append("\nOrigine : " + itineraires.get(s).getGareDepart() + " - Horaire : "
						+ itineraires.get(s).getHeureArriveeEnGare().toLocalTime());
			}
		}
		sb.append("\n- Departs -");
		for (String s : itineraires.keySet()) {
			if (itineraires.get(s).getHeureDepartDeGare() != null) {
				sb.append("\nDestination : " + itineraires.get(s).getGareArrive() + " - Horaire : "
						+ itineraires.get(s).getHeureDepartDeGare().toLocalTime());
			}
		}
		sb.append("\n");
		System.out.println(sb.toString());
	}

	public String getGare() {
		return gare;
	}

	public void setGare(String gare) {
		this.gare = gare;
	}

	public void addStop(Itineraire i) {
		itineraires.put(i.getIdItineraire(), i);
	}

	public void updateStop(Itineraire i) {
		itineraires.replace(i.getIdItineraire(), i);
	}
}
