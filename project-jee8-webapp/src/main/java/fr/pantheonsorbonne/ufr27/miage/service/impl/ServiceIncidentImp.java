package fr.pantheonsorbonne.ufr27.miage.service.impl;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import fr.pantheonsorbonne.ufr27.miage.jpa.Incident;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Incident.CodeEtatIncident;
import fr.pantheonsorbonne.ufr27.miage.jpa.Incident.CodeTypeIncident;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.mapper.IncidentMapper;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.IncidentJAXB;
import fr.pantheonsorbonne.ufr27.miage.repository.IncidentRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceIncident;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceMajDecideur;
import fr.pantheonsorbonne.ufr27.miage.service.utils.Retard;

@ManagedBean
@RequestScoped
public class ServiceIncidentImp implements ServiceIncident {

	@Inject
	ServiceMajDecideur serviceMajDecideur;

	@Inject
	IncidentRepository incidentRepository;

	@Inject
	ItineraireRepository itineraireRepository;

	@Override
	public boolean creerIncident(int idTrain, IncidentJAXB incidentJAXB) {
		Incident incident = IncidentMapper.mapIncidentJAXBToIncident(incidentJAXB);
		incidentRepository.creerIncident(idTrain, incident);
		Itineraire itineraire = itineraireRepository.getItineraireByTrainEtEtat(idTrain,
				CodeEtatItinieraire.EN_INCIDENT);
		LocalTime estimationRetard = estimationTempsRetard(incidentJAXB.getTypeIncident());
		Retard retard = new Retard(itineraire, estimationRetard);
		serviceMajDecideur.decideRetard(retard, true);
		if (estimationRetard.isAfter(LocalTime.of(2, 0, 0, 0))) {
			serviceMajDecideur.affecterUnAutreTrainAuxArretsDeItineraire(itineraire);
		}
		return true;
	}

	/**
	 * Si besoin, cf. la description de cette méthode dans l'interface
	 * ServiceIncident pour bien la comprendre
	 */
	@Override
	public boolean majEtatIncident(int idTrain, int etatIncident, long ajoutDuree, ChronoUnit chronoUnitDuree) {
		boolean res = false;
		Incident incident = incidentRepository.getIncidentByIdTrain(idTrain);
		Itineraire itineraire = itineraireRepository.getItineraireByTrainEtEtat(idTrain,
				CodeEtatItinieraire.EN_INCIDENT);
		LocalDateTime now = LocalDateTime.now();
		if (etatIncident == CodeEtatIncident.EN_COURS.getCode()) {
			if (now.isAfter(incident.getHeureTheoriqueDeFin())) {
				LocalDateTime oldEnd = incident.getHeureTheoriqueDeFin();
				incidentRepository.majHeureDeFinIncident(incident, oldEnd.plus(ajoutDuree, chronoUnitDuree));
				LocalTime dureeProlongation = LocalTime.MIN.plus(ajoutDuree, chronoUnitDuree);
				serviceMajDecideur.decideRetard(new Retard(itineraire, dureeProlongation), true);
				if (incident.getHeureTheoriqueDeFin()
						.isAfter(now.plusSeconds(LocalTime.of(2, 0, 0, 0).toSecondOfDay()))) {
					serviceMajDecideur.affecterUnAutreTrainAuxArretsDeItineraire(itineraire);
				}
			}
			res = true;
		} else if (etatIncident == CodeEtatIncident.RESOLU.getCode()) {
			// On remet l'itinéraire à l'état en cours
			incidentRepository.majEtatIncident(incident, CodeEtatIncident.RESOLU);
			itineraireRepository.majEtatItineraire(itineraire, CodeEtatItinieraire.EN_COURS);
			// Si on a rallongé l'incident de 5min et qu'au bout d'1min il est finalement
			// résolu, on veut raccourcir les retards concernés de 4min
			LocalTime regulation = incident.getHeureTheoriqueDeFin().minusSeconds(now.toLocalTime().toSecondOfDay())
					.toLocalTime();
			serviceMajDecideur.decideRetard(new Retard(itineraire, regulation), false);
			res = true;
		}
		return res;
	}

	/**
	 * Récupérer l'estimation du temps de retard associée au type de retard
	 * définie dans l'énumération CodeTypeIncident
	 * @param codeTypeIncident
	 * @return
	 */
	private LocalTime estimationTempsRetard(int codeTypeIncident) {
		return CodeTypeIncident.getTempEstimation(codeTypeIncident);
	}
}
