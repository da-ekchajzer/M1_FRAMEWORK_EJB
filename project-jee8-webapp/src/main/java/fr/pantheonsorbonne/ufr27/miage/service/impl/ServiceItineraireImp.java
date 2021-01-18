package fr.pantheonsorbonne.ufr27.miage.service.impl;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.mapper.ItineraireMapper;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ArretJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ItineraireJAXB;
import fr.pantheonsorbonne.ufr27.miage.repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceItineraire;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceMajInfoGare;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceUtilisateur;

@ManagedBean
@RequestScoped
public class ServiceItineraireImp implements ServiceItineraire {

	@Inject
	ServiceUtilisateur serviceUtilisateur;

	@Inject
	ServiceMajInfoGare serviceMajInfoGareImp;

	@Inject
	ItineraireRepository itineraireRepository;

	@Override
	public ItineraireJAXB getItineraire(int idTrain) {
		Itineraire itineraire = itineraireRepository.recupItineraireEnCoursOuLeProchain(idTrain);
		if (itineraire != null) {
			if (itineraire.getEtat() == CodeEtatItinieraire.EN_ATTENTE.getCode()) {
				itineraireRepository.majEtatItineraire(itineraire, CodeEtatItinieraire.EN_COURS);
				serviceUtilisateur.initUtilisateursItineraire(idTrain);
				serviceMajInfoGareImp.publishItineraire(itineraire);
			}
			return ItineraireMapper.mapItineraireToItineraireJAXB(itineraire);
		}
		return null;
	}

	@Override
	public boolean majItineraire(int idTrain, ArretJAXB arretJAXB) {
		// Récupérer l'itinéraire associé à l'idTrain
		Itineraire itineraire = itineraireRepository.getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);
		// Récupérer l'arrêt de l'itinéraire qui a pour nom a.getGare().getNom()
		Arret arret = null;
		for (Arret a : itineraire.getArretsDesservis()) {
			if (a.getGare().getNom().equals(arretJAXB.getGare())) {
				arret = a;
				break;
			}
		}
		itineraireRepository.majArretActuel(itineraire, arret);
		serviceUtilisateur.majUtilisateursTrain(idTrain);
		if (itineraire.getNextArret() == null) {
			itineraireRepository.majEtatItineraire(itineraire, CodeEtatItinieraire.FIN);
		}
		return true;
	}

}
