package fr.pantheonsorbonne.ufr27.miage.n_service.impl;

import java.time.LocalTime;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;

@ManagedBean
@RequestScoped
public class ServiceMajExecuteurImp implements fr.pantheonsorbonne.ufr27.miage.n_service.ServiceMajExecuteur {

	@Inject
	ServiceMajDecideurImp serviceMajDecideur;
	
	@Override
	public void supprimerArret(Train train, int idArret) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retarderTrain(Train train, LocalTime tempsRetard) {
		// TODO Auto-generated method stub
		serviceMajDecideur.decideMajTrainEnCours(train.getId(), tempsRetard);
	}

	@Override
	public void ajouterArret(Train train, Arret Arret) {
		// TODO Auto-generated method stub
	}

}
