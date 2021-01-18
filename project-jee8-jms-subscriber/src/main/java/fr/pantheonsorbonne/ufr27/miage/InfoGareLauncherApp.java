package fr.pantheonsorbonne.ufr27.miage;

import javax.jms.JMSException;

import fr.pantheonsorbonne.ufr27.miage.infogare.InfoGare;

public class InfoGareLauncherApp {

	public static void main(String[] args) throws InterruptedException, JMSException {

		String[] gare = { "Paris - Gare de Lyon", "Avignon-Centre", "Aix en Provence", "Marseille - St Charles",
				"Dijon-Ville", "Lyon - Pardieu", "Narbonne", "Sete", "Perpignan", "Paris - Montparnasse", "Tours",
				"Bordeaux - Saint-Jean", "Pessac", "Arcachon-Centre", "Nantes", "Montpellier", "Cabries"};
		int infogareId = 0;

		Thread.sleep(10000);

		while (infogareId < gare.length) {
			InfoGare infoGare = new InfoGare(gare[infogareId]);
			Thread thread = new Thread(infoGare);
			thread.start();
			infogareId++;
		}
	}
}
