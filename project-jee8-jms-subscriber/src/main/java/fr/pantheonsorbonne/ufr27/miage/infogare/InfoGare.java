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
	String affichageNomGare;
	InfoGareProcessorBean processor;
	Map<String, Itineraire> itineraires;

	public InfoGare(String gare) {
		this.gare = gare;
		this.itineraires = new HashMap<String, Itineraire>();
		setUpAffichageNomGare();

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
		LocalDateTime now = LocalDateTime.now();
		for (String s : itineraires.keySet()) {
			if (itineraires.get(s).getHeureArriveeEnGare() == null) {
				if (now.isAfter(itineraires.get(s).getHeureDepartDeGare().plusSeconds(45))) {
					itineraires.remove(s);
				}
			} else if (itineraires.get(s).getHeureDepartDeGare() == null) {
				if (now.isAfter(itineraires.get(s).getHeureArriveeEnGare())) {
					itineraires.remove(s);
				}
			} else {
				if (now.isAfter(itineraires.get(s).getHeureDepartDeGare())) {
					itineraires.remove(s);
				}
			}
		}
	}

	private void affichage() {
		if (!itineraires.keySet().isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append(affichageNomGare);
			String tabArrivees = "\n---> Arrivées <----\n";
			sb.append(tabArrivees);
			int total = sb.length();
			for (String s : itineraires.keySet()) {
				if (itineraires.get(s).getHeureArriveeEnGare() != null) {
					sb.append("\t" + itineraires.get(s).getIdItineraire() + " ( "
							+ itineraires.get(s).getHeureArriveeEnGare().toLocalTime() + " )\t\tOrigine :\t\t# "
							+ itineraires.get(s).getGareDepart() + "\n");
				}
			}
			if (total == sb.length()) {
				sb.delete(sb.length() - tabArrivees.length(), sb.length());
			}
			String tabDeparts = "\n----> Departs <----\n";
			sb.append(tabDeparts);
			for (String s : itineraires.keySet()) {
				if (itineraires.get(s).getHeureDepartDeGare() != null) {
					sb.append("\t" + itineraires.get(s).getIdItineraire() + " ( "
							+ itineraires.get(s).getHeureDepartDeGare().toLocalTime() + " )\t\tDestination :");
					for (String gare : itineraires.get(s).getGaresDesservies()) {
						sb.append("\n\t\t\t\t\t\t\t\t| " + gare);
					}
					sb.append("\n\t\t\t\t\t\t\t\tv\n");
				}
			}
			if (total == sb.length()) {
				sb.delete(sb.length() - tabDeparts.length(), sb.length());
			}
			System.out.println(sb.toString());
		}
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

	public void setUpAffichageNomGare() {
		StringBuilder sb = new StringBuilder();
		int ajusteur = 0, debut = 0, fin = 0;
		String base = "==========================================================================================";
		ajusteur = base.length() - this.gare.length() - 2;
		debut = ajusteur / 2;
		fin = ajusteur / 2 + ajusteur % 2;
		sb.append(base.substring(0, debut) + " " + this.gare + " " + base.subSequence(0, fin));
		this.affichageNomGare = sb.toString();
	}
}
