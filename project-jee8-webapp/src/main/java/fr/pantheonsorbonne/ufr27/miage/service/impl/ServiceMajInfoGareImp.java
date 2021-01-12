package fr.pantheonsorbonne.ufr27.miage.service.impl;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.jms.MessageGateway;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceMajInfoGare;

@ManagedBean
@ApplicationScoped
public class ServiceMajInfoGareImp implements ServiceMajInfoGare {

	@Inject
	MessageGateway messageGateway;
	
	@Inject
	ItineraireRepository itineraireRepository;
	
	@Override
	public void majHoraireTrain(int idTrain) {
		Itineraire i = itineraireRepository.getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);
		messageGateway.publishMaj(i);
	}

}