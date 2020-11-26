package fr.pantheonsorbonne.ufr27.miage.n_service.impl;

import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.n_dao.VoyageurDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceUtilisateur;

public class ServiceUtilisateurImp implements ServiceUtilisateur{

	@Inject
	VoyageurDAO voyageurDAO;


	@Override
	public void initUtilisateursItineraire(int idTrain) {
		voyageurDAO.mettreVoyageursDansItineraire(idTrain);
	}

	
	@Override
	public void majUtilisateursTrain(int idTrain, Arret arret) {
		voyageurDAO.majVoyageursDansTrainAvecResa(idTrain, arret);
	}

}
