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
			if (itineraire.getEtat() == CodeEtatItinieraire.EN_ATTENTE.getCode()) {
				itineraireRepository.majEtatItineraire(itineraire, CodeEtatItinieraire.EN_COURS);
				itineraireRepository.majArretActuel(itineraire, itineraire.getArretsDesservis().get(0));
				serviceUtilisateur.initUtilisateursItineraire(idTrain);
			}
			return ItineraireMapper.mapItineraireToItineraireJAXB(itineraire);
		}
		return null;
	}

	@Override
	public boolean majItineraire(int idTrain, ArretJAXB a) {
		// Récupérer l'itinéraire associé à l'idTrain
		Itineraire itineraire = itineraireRepository.getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);
		// Récupérer l'arrêt de l'itinéraire qui a pour nom a.getGare().getNom()
		Arret arret = this.getArretByItineraireAndNomGare(itineraire, a.getGare());
		itineraireRepository.majArretActuel(itineraire, arret);
		//serviceUtilisateur.majUtilisateursTrain(idTrain);
		if (itineraireRepository.getNextArretByItineraireEtArretActuel(itineraire, arret) == null) {
			itineraireRepository.majEtatItineraire(itineraire, CodeEtatItinieraire.FIN);
		}
		return true;
	}

	private Arret getArretByItineraireAndNomGare(Itineraire itineraire, String nomGare) {
		Arret arret = null;
		for (Arret a : itineraire.getArretsDesservis()) {
			if (a.getGare().getNom().equals(nomGare)) {
				arret = a;
			}
		}
		return arret;
	}

}
