package fr.pantheonsorbonne.ufr27.miage.n_repository;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.n_dao.IncidentDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;

@ManagedBean
@RequestScoped
public class IncidentRepository {

	@Inject
	IncidentDAO incidentDAO;

	@Inject
	ItineraireRepository itineraireRepository;

	public List<Incident> getAllIncidents() {
		return incidentDAO.getAllIncidents();
	}

	public int getNbIncidents() {
		return incidentDAO.getNbIncidents();
	}

	public Incident getIncidentById(int idIncident) {
		return incidentDAO.getIncidentById(idIncident);
	}

	public boolean creerIncident(int idTrain, Incident incident) {
		// On récupère le nb d'Incidents en BD avant l'insertion
		int nbIncidentsAvantAjout = this.getNbIncidents();

		incidentDAO.ajouterIncidentEnBD(incident);

		// On récupère le nb d'Incidents en BD après l'insertion
		int nbIncidentsApresAjout = this.getNbIncidents();

		// On vérifie que l'insertion a été effectuée
		if (nbIncidentsApresAjout != nbIncidentsAvantAjout + 1) {
			// LOG.error
			return false;
		}

		// Récupération de l'itinéraire EN COURS (=1) de TRAIN_ID idTrain
		Itineraire itineraire = itineraireRepository.getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);

		// Ajout de l'INCIDENT_ID dans l'itinéraire associé au train
		ajouterIncidentItineraire(itineraire.getId(), incident.getId());

		return true;
	}

	public void ajouterIncidentItineraire(int idItineraire, int idIncident) {
		Itineraire itineraire = itineraireRepository.getItineraireById(idItineraire);
		Incident incident = getIncidentById(idIncident);
		incidentDAO.associerIncidentItineraire(itineraire, incident);
	}

	public void updateEtatIncident(int idTrain, int etat) {
		// Récupération de l'itinéraire EN COURS (=1) de TRAIN_ID idTrain
		Itineraire itineraire = itineraireRepository.getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);

		// Récupération de l'incident associé à l'itinéraire itinéraire
		Incident incident = getIncidentById(itineraire.getIncident().getId());

		incidentDAO.majEtatIncidentEnBD(incident, etat);
	}

}
