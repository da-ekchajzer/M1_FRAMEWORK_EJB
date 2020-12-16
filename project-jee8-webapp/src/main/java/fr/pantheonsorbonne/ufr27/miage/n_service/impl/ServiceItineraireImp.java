package fr.pantheonsorbonne.ufr27.miage.n_service.impl;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ArretJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ItineraireJAXB;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.n_mapper.ItineraireMapper;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceItineraire;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceUtilisateur;

@ManagedBean
@RequestScoped
public class ServiceItineraireImp implements ServiceItineraire {

	@Inject
	ServiceUtilisateur serviceUtilisateur;

	@Inject
	ItineraireRepository itineraireRepository;

	@Override
	public ItineraireJAXB getItineraire(int idTrain) {
		Itineraire itineraire = itineraireRepository.recupItineraireEnCoursOuLeProchain(idTrain);
		if (itineraire != null) {
			// Il faut update l'itinéraire de état = 0 à état = 1 !
			if (itineraire.getEtat() == CodeEtatItinieraire.EN_ATTENTE.getCode()) {
				itineraireRepository.majEtatItineraire(itineraire, CodeEtatItinieraire.EN_COURS);
				
				//Passer l'arret actuel a la gare de depart de l'itineraire
				itineraireRepository.majArretActuel(idTrain, itineraire.getArretsDesservis().get(0));
				
				serviceUtilisateur.initUtilisateursItineraire(idTrain);
			}
		}
		return ItineraireMapper.mapItineraireToItineraireJAXB(itineraire);
	}

	@Override
	public boolean majItineraire(int idTrain, ArretJAXB a) {
		// Récupérer l'itinéraire associé à l'idTrain
		Itineraire itineraire = itineraireRepository.getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);
		
		// Récupérer l'arrêt de l'itinéraire qui a pour nom a.getGare().getNom()
		Arret arret = this.getArretByItineraireAndNomGare(itineraire, a.getGare());
	
		itineraireRepository.majArretActuel(idTrain, arret);
		
		serviceUtilisateur.majUtilisateursTrain(idTrain);
		return true;
	}
	
	private Arret getArretByItineraireAndNomGare(Itineraire itineraire, String nomGare) {
		Arret arret = null;
		for(Arret a : itineraire.getArretsDesservis()) {
			if(a.getGare().getNom().equals(nomGare)) {
				arret = a;
			}
		}
		return arret;
	}

}
