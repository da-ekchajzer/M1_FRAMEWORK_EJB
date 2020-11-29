package fr.pantheonsorbonne.ufr27.miage.n_service.impl;

import javax.inject.Inject;
import fr.pantheonsorbonne.ufr27.miage.n_repository.VoyageurRepository;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceUtilisateur;

public class ServiceUtilisateurImp implements ServiceUtilisateur{

	@Inject
	VoyageurRepository voyageurRepository;


	@Override
	public void initUtilisateursItineraire(int idTrain) {
		voyageurRepository.mettreVoyageursDansItineraire(idTrain);
	}

	
	@Override
	public void majUtilisateursTrain(int idTrain) {
		voyageurRepository.majVoyageursDansTrainAvecResa(idTrain);
	}

}
