package fr.pantheonsorbonne.ufr27.miage.infogare;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	List<String> itinerairesToRemove;

	public InfoGare(String gare) {
		this.gare = gare;
		this.itineraires = new HashMap<String, Itineraire>();
		this.itinerairesToRemove = new ArrayList<String>();
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
		itinerairesToRemove = new ArrayList<String>();
		LocalDateTime now = LocalDateTime.now();
		for (String s : itineraires.keySet()) {
			if (itineraires.get(s).getHeureArriveeEnGare() == null) {
				if (now.isAfter(itineraires.get(s).getHeureDepartDeGare().plusSeconds(45))) {
					itinerairesToRemove.add(s);
				}
			} else if (itineraires.get(s).getHeureDepartDeGare() == null) {
				if (now.isAfter(itineraires.get(s).getHeureArriveeEnGare())) {
					itinerairesToRemove.add(s);
				}
			} else {
				if (now.isAfter(itineraires.get(s).getHeureDepartDeGare())) {
					itinerairesToRemove.add(s);
				}
			}
		}
		for (String s : itinerairesToRemove) {
			itineraires.remove(s);
		}
		itinerairesToRemove.clear();
	}

	private void affichage() {
		if (!itineraires.keySet().isEmpty()) {
			String itineraire = "";
			StringBuilder sb = new StringBuilder();
			sb.append(affichageNomGare);
			String tabArrivees = "\n---> Arrivées <----\n";
			sb.append(tabArrivees);
			int total = sb.length();
			for (String s : itineraires.keySet()) {
				if (itineraires.get(s).getHeureArriveeEnGare() != null) {
					itineraire = itineraires.get(s).getIdItineraire();
					itineraire = itineraire.length() == 3 ? itineraire + " " : itineraire;
					sb.append("\t" + itineraire + " ( " + itineraires.get(s).getHeureArriveeEnGare().toLocalTime()
							+ " )\t\tOrigine :\t\t# " + itineraires.get(s).getGareDepart() + "\n");
				}
			}
			if (total == sb.length()) {
				sb.delete(sb.length() - tabArrivees.length(), sb.length());
			}
			String tabDeparts = "\n----> Departs <----\n";
			sb.append(tabDeparts);
			total = sb.length();
			for (String s : itineraires.keySet()) {
				if (itineraires.get(s).getHeureDepartDeGare() != null) {
					itineraire = itineraires.get(s).getIdItineraire();
					itineraire = itineraire.length() == 3 ? itineraire + " " : itineraire;
					sb.append("\t" + itineraire + " ( " + itineraires.get(s).getHeureDepartDeGare().toLocalTime()
							+ " )\t\tDestination :");
					int count = 0;
					for (String gare : itineraires.get(s).getGaresDesservies()) {
						if (count == 0) {
							sb.append("\t\t| " + gare);
							count++;
						} else {
							sb.append("\n\t\t\t\t\t\t\t\t| " + gare);
						}
					}
					sb.append("\n\t\t\t\t\t\t\t\tv\n\n");
				}
			}
			if (total == sb.length()) {
				sb.delete(sb.length() - tabDeparts.length(), sb.length());
			} else {
				sb.delete(sb.length() - 1, sb.length());
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
