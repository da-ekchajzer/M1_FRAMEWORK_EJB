package fr.pantheonsorbonne.ufr27.miage.n_service.impl;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ArretJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.IncidentJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ItineraireJAXB;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceItineraire;

public class ServiceItineraireImp implements ServiceItineraire {

	@Override
	public Boolean ItineraireExist(int idTrain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItineraireJAXB getInitineraire(int idTrain) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void majItineraire(int idTrain, ArretJAXB a) {
		// TODO Auto-generated method stub
		
	}
	
	//-----------------Modifs Sophia ----------------//
	
	@SuppressWarnings("unused")
	private void updateArret(int idTrain) {
		// TODO Auto-generated method stub
		// Recuperer l'arret auquel on se situe et maj le nom de l'arret
	}
	

}
