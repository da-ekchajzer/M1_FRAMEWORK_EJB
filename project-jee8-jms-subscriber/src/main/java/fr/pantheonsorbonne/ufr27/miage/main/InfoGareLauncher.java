package fr.pantheonsorbonne.ufr27.miage.main;

import javax.enterprise.inject.se.SeContainer;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSException;
import javax.jms.Queue;

import fr.pantheonsorbonne.ufr27.miage.n_jms.InfoGareProcessorBean;

public class InfoGareLauncher {
	
	public static void main(String[] args) throws InterruptedException, JMSException {
		
		String[] gare = { "Paris-Montparnasse", "Lyon Pardieux" };
		int infogareId = 0;

		
		while (infogareId < gare.length) {
			InfoGare infoGare = new InfoGare(gare[infogareId]);
			Thread thread = new Thread(infoGare);
			thread.start();
			infogareId++;
		}

	}

}
