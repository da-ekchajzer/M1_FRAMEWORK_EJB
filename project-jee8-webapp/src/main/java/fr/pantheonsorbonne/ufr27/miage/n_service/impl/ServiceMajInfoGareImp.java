package fr.pantheonsorbonne.ufr27.miage.n_service.impl;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.n_jms.MessageGateway;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceMajInfoGare;

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
