package fr.pantheonsorbonne.ufr27.miage.main;

import javax.jms.JMSException;

public class InfoGareLauncher {

	public static void main(String[] args) throws InterruptedException, JMSException {
		
		String[] gare = { "Paris-Montparnasse", "Lyon Pardieux" };
		int infogareId = 0;
		
		while (infogareId < gare.length) {
			InfoGare infoGare = new InfoGare(gare[infogareId]);
			Thread thread = new Thread(infoGare);
			thread.start();
		}

	}

}
