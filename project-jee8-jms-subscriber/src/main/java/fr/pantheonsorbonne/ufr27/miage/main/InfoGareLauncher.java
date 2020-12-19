package fr.pantheonsorbonne.ufr27.miage.main;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSException;
import javax.jms.Queue;

public class InfoGareLauncher {

	@Inject
	@Named("ItineraireAckQueue")
	private static Queue queueAck;

	@Inject
	@Named("ItinerairePubQueue")
	private static Queue queueInfoPub;
	
	public static void main(String[] args) throws InterruptedException, JMSException {
		
		String[] gare = { "Paris-Montparnasse", "Lyon Pardieux" };
		int infogareId = 0;
		
		while (infogareId < gare.length) {
			InfoGare infoGare = new InfoGare(gare[infogareId], queueAck, queueInfoPub);
			Thread thread = new Thread(infoGare);
			thread.start();
		}

	}

}
