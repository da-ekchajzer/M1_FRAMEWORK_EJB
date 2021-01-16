package fr.pantheonsorbonne.ufr27.miage.service.impl;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.jms.MessageGateway;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceMajInfoGare;

@ManagedBean
@RequestScoped
public class ServiceMajInfoGareImp implements ServiceMajInfoGare {

	@Inject
	MessageGateway messageGateway;

	@Inject
	ItineraireRepository itineraireRepository;

	@Override
	public void majHoraireItineraire(Itineraire itineraire) {
		messageGateway.publishMaj(itineraire);
	}

	@Override
	public void publishItineraire(Itineraire itineraire) {
		messageGateway.publishCreation(itineraire);
	}

}
