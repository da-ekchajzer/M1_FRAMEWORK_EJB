package fr.pantheonsorbonne.ufr27.miage.n_service.impl;

import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ArretJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ItineraireJAXB;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceItineraire;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceUtilisateur;

public class ServiceItineraireImp implements ServiceItineraire {

	@Inject
	ServiceUtilisateur serviceUtilisateur;

	@Override
	public ItineraireJAXB getItineraire(int idTrain) {
		// TODO appeler le DAO
		return null;
	}

	@Override
	public boolean majItineraire(int idTrain, ArretJAXB a) {
		Arret arret = getArretfromXML(a);
		
		updateArret(idTrain, arret);
		serviceUtilisateur.majUtilisateursTrain(idTrain, arret);
		
		return true;

	}

	private void updateArret(int idTrain, Arret a) {
		// TODO Auto-generated method stub
		// Recuperer l'arret auquel on se situe et maj le nom de l'arret

	}
	
	private Arret getArretfromXML(ArretJAXB a) {
		
		return null;
	}
	

}
