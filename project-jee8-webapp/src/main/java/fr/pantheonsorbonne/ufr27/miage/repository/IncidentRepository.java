package fr.pantheonsorbonne.ufr27.miage.repository;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.dao.IncidentDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.Incident;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Incident.CodeEtatIncident;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire.CodeEtatItinieraire;

@ManagedBean
@RequestScoped
public class IncidentRepository {

	@Inject
	IncidentDAO incidentDAO;

	@Inject
	ItineraireRepository itineraireRepository;

	/**
	 * Récupérer l'ensemble des incidents présents en BD
	 * @return
	 */
	public List<Incident> getAllIncidents() {
		return incidentDAO.getAllIncidents();
	}

	/**
	 * Récupérer l'incident ayant pour id idIncident
	 * @param idIncident
	 * @return
	 */
	public Incident getIncidentById(int idIncident) {
		return incidentDAO.getIncidentById(idIncident);
	}

	/**
	 * Créer un incident
	 * => l'ajouter en BD
	 * => l'associer à l'itinéraire associé au train d'id idTrain puis
	 * passer cet itinéraire de l'état EN_COURS à l'état EN_INCIDENT
	 * @param idTrain
	 * @param incident
	 * @return
	 */
	public Incident creerIncident(int idTrain, Incident incident) {
		incidentDAO.ajouterIncidentEnBD(incident);
		// Ajout de l'INCIDENT_ID dans l'itinéraire associé au train
		Itineraire itineraire = itineraireRepository.getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);
		itineraireRepository.majEtatItineraire(itineraire, CodeEtatItinieraire.EN_INCIDENT);
		incidentDAO.associerIncidentItineraire(itineraire, incident);
		return incident;
	}

	/**
	 * Mettre à jour l'état de l'incident (EN_COURS/RESOLU)
	 * @param incident
	 * @param newEtat
	 */
	public void majEtatIncident(Incident incident, CodeEtatIncident newEtat) {
		incidentDAO.majEtatIncidentEnBD(incident, newEtat);
	}

	/**
	 * Modifier l'heure de fin de l'incident en lui passant
	 * comme nouvelle valeur celle passée en paramètre
	 * @param incident
	 * @param newHeureDeFin
	 */
	public void majHeureDeFinIncident(Incident incident, LocalDateTime newHeureDeFin) {
		incidentDAO.majHeureDeFinEnBD(incident, newHeureDeFin);
	}

	/**
	 * Récupérer l'incident associé à l'itinéraire (à l'état EN_INCIDENT, donc)
	 * qui lui-même est associé au train d'id idTrain
	 * @param idTrain
	 * @return
	 */
	public Incident getIncidentByIdTrain(int idTrain) {
		// Récupération de l'itinéraire EN INCIDENT (=2) de TRAIN_ID idTrain
		Itineraire itineraire = itineraireRepository.getItineraireByTrainEtEtat(idTrain,
				CodeEtatItinieraire.EN_INCIDENT);
		// Récupération de l'incident associé à l'itinéraire itinéraire
		Incident incident = getIncidentById(itineraire.getIncident().getId());
		return incident;
	}

}
