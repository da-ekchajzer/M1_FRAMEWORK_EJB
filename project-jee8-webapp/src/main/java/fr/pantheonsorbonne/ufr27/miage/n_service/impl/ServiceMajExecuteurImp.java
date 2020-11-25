package fr.pantheonsorbonne.ufr27.miage.n_service.impl;

import java.sql.Time;

import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;

public class ServiceMajExecuteurImp implements fr.pantheonsorbonne.ufr27.miage.n_service.ServiceMajExecuteur {

	@Inject
	ServiceMajDecideurImp serviceMajDecideur;
	
	@Override
	public void supprimerArret(Train train, int idArret) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retarderTrain(Train train, Time tempsRetard) {
		// TODO Auto-generated method stub
		serviceMajDecideur.decideMajTrainEnCours(train.getId(), tempsRetard);
	}

	@Override
	public void ajouterArret(Train train, Arret Arret) {
		// TODO Auto-generated method stub
	}

}
