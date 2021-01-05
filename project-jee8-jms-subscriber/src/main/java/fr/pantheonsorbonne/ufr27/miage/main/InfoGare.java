package fr.pantheonsorbonne.ufr27.miage.main;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.xml.bind.JAXBException;

import fr.pantheonsorbonne.ufr27.miage.POJO.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jms.InfoGareProcessorBean;

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
			} catch (JMSException | JAXBException e) {
				e.printStackTrace();
			}
		}
	}

	public String getGare() {
		return gare;
	}

	public void setGare(String gare) {
		this.gare =  gare;
	}
	
	public void addStop(Itineraire i) {
		itineraires.put(i.getIdItineraire(), i);
	}
	
	public void updateStop(Itineraire i) {
		itineraires.replace(i.getIdItineraire(), i);
	}
}
