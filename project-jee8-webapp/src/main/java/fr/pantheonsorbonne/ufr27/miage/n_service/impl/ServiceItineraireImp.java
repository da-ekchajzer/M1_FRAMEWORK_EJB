package fr.pantheonsorbonne.ufr27.miage.n_service.impl;

import javax.inject.Inject;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ArretJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ItineraireJAXB;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_mapper.ArretMapper;
import fr.pantheonsorbonne.ufr27.miage.n_mapper.ItineraireMapper;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceItineraire;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceUtilisateur;

public class ServiceItineraireImp implements ServiceItineraire {

	@Inject
	ServiceUtilisateur serviceUtilisateur;

	@Inject
	ItineraireRepository itineraireRepository;

	@Override
	public ItineraireJAXB getItineraire(int idTrain) {
		Itineraire itineraire = itineraireRepository.recupItineraireEnCoursOuLeProchain(idTrain);
		if (itineraire != null) {
			System.out.println("== itineraire n'est pas nul ==");
			// Il faut update l'itinéraire de état = 0 à état = 1 !
			serviceUtilisateur.initUtilisateursItineraire(idTrain);
		}
		return ItineraireMapper.mapItineraireToItineraireJAXB(itineraire);
	}

	@Override
	public boolean majItineraire(int idTrain, ArretJAXB a) {
		Arret arret = ArretMapper.mapArretJAXBToArret(a);
		updateArret(idTrain, arret);
		serviceUtilisateur.majUtilisateursTrain(idTrain);
		return true;
	}

	private void updateArret(int idTrain, Arret a) {
		itineraireRepository.majArretActuel(idTrain, a);
	}

}
