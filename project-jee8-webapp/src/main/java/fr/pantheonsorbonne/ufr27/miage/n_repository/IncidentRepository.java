package fr.pantheonsorbonne.ufr27.miage.n_repository;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.n_dao.IncidentDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident.CodeEtatIncident;
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
		boolean res = true;
		// On récupère le nb d'Incidents en BD avant l'insertion
		int nbIncidentsAvantAjout = this.getNbIncidents();
		incidentDAO.ajouterIncidentEnBD(incident);
		// On récupère le nb d'Incidents en BD après l'insertion
		int nbIncidentsApresAjout = this.getNbIncidents();

		// On vérifie que l'insertion a été effectuée
		if (nbIncidentsApresAjout != nbIncidentsAvantAjout + 1) {
			// LOG.error
			res = false;
		}

		// Ajout de l'INCIDENT_ID dans l'itinéraire associé au train
		Itineraire itineraire = itineraireRepository.getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);
		associerIncidentItineraire(itineraire, incident);
		return res;
	}

	private void associerIncidentItineraire(Itineraire itineraire, Incident incident) {
		itineraireRepository.majEtatItineraire(itineraire, CodeEtatItinieraire.EN_INCIDENT);
		incidentDAO.associerIncidentItineraire(itineraire, incident);
	}

	public void majEtatIncident(Incident incident, CodeEtatIncident newEtat) {
		incidentDAO.majEtatIncidentEnBD(incident, newEtat);
	}

	public void majHeureDeFinIncident(Incident incident, LocalDateTime newHeureDeFin) {
		incidentDAO.majHeureDeFinEnBD(incident, newHeureDeFin);
	}

	public Incident getIncidentByIdTrain(int idTrain) {
		// Récupération de l'itinéraire EN INCIDENT (=2) de TRAIN_ID idTrain
		Itineraire itineraire = itineraireRepository.getItineraireByTrainEtEtat(idTrain,
				CodeEtatItinieraire.EN_INCIDENT);
		// Récupération de l'incident associé à l'itinéraire itinéraire
		Incident incident = getIncidentById(itineraire.getIncident().getId());
		return incident;
	}

}
