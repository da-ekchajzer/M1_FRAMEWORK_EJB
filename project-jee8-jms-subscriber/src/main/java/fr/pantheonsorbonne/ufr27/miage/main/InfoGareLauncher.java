package fr.pantheonsorbonne.ufr27.miage.main;

import javax.jms.JMSException;

public class InfoGareLauncher {
	
	public static void main(String[] args) throws InterruptedException, JMSException {
		
		String[] gare = { "Tours", "Bordeaux - Saint-Jean" };
		int infogareId = 0;

		
		while (infogareId < gare.length) {
			InfoGare infoGare = new InfoGare(gare[infogareId]);
			Thread thread = new Thread(infoGare);
			thread.start();
			infogareId++;
		}

	}

}
