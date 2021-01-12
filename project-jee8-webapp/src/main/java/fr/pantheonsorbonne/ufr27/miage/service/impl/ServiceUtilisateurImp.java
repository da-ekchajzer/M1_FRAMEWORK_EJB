package fr.pantheonsorbonne.ufr27.miage.service.impl;

import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.repository.TrainRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.VoyageurRepository;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceUtilisateur;

public class ServiceUtilisateurImp implements ServiceUtilisateur {

	@Inject
	VoyageurRepository voyageurRepository;
	
	@Inject
	TrainRepository trainRepository;

	@Override
	public void initUtilisateursItineraire(int idTrain) {
		voyageurRepository.mettreVoyageursDansItineraire(idTrain);
	}

	@Override
	public void majUtilisateursTrain(int idTrain) {
		Train train = trainRepository.getTrainById(idTrain);
		if (train instanceof TrainAvecResa) {
			voyageurRepository.majVoyageursDansTrainAvecResa(train);
		}
	}
	
}
