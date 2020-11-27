package fr.pantheonsorbonne.ufr27.miage.n_service.impl;

import javax.inject.Inject;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ArretJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ItineraireJAXB;
import fr.pantheonsorbonne.ufr27.miage.n_dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_mapper.ArretMapper;
import fr.pantheonsorbonne.ufr27.miage.n_mapper.ItineraireMapper;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceItineraire;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceUtilisateur;

public class ServiceItineraireImp implements ServiceItineraire {

	@Inject
	ServiceUtilisateur serviceUtilisateur;
	
	@Inject
	ItineraireDAO itineraireDAO;

	@Override
	public ItineraireJAXB getItineraire(int idTrain) {
		serviceUtilisateur.initUtilisateursItineraire(idTrain);
		Itineraire itineraire = itineraireDAO.recupItineraireEnCoursOuLeProchain(idTrain);
		return ItineraireMapper.mapItineraireToItineraireJAXB(itineraire);
	}

	@Override
	public boolean majItineraire(int idTrain, ArretJAXB a) {
		Arret arret = ArretMapper.mapArretJAXBToArret(a);
		updateArret(idTrain, arret);
		serviceUtilisateur.majUtilisateursTrain(idTrain, arret);
		return true;
	}

	private void updateArret(int idTrain, Arret a) {
		itineraireDAO.majArretActuel(idTrain, a);
	}


}
