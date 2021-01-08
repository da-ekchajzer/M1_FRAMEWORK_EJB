package fr.pantheonsorbonne.ufr27.miage.n_service.impl;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.IncidentJAXB;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident.CodeEtatIncident;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident.CodeTypeIncident;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.n_mapper.IncidentMapper;
import fr.pantheonsorbonne.ufr27.miage.n_repository.IncidentRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.TrainRepository;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceIncident;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceMajDecideur;
import fr.pantheonsorbonne.ufr27.miage.n_service.utils.Retard;

@ManagedBean
@RequestScoped
public class ServiceIncidentImp implements ServiceIncident {
	@Inject
	ServiceMajDecideur serviceMajDecideur;

	@Inject
	IncidentRepository incidentRepository;

	@Inject
	ItineraireRepository itineraireRepository;

	@Inject
	TrainRepository trainRepository;

	@Override
	public boolean creerIncident(int idTrain, IncidentJAXB inc) {
		Incident i = IncidentMapper.mapIncidentJAXBToIncident(inc);
		incidentRepository.creerIncident(idTrain, i);
		Retard r = new Retard(itineraireRepository.getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS),
				estimationTempsRetard(inc.getTypeIncident()));
		serviceMajDecideur.decideRetard(r);
		return true;
	}

	@Override
	public boolean majEtatIncident(int idTrain, int newEtatIncident, long ajoutDuree, ChronoUnit chronoUnit) {
		boolean res = false;
		Incident incident = this.incidentRepository.getIncidentByIdTrain(idTrain);
		Itineraire itineraire = this.itineraireRepository.getItineraireByTrainEtEtat(idTrain,
				CodeEtatItinieraire.EN_COURS);
		if (newEtatIncident == CodeEtatIncident.EN_COURS.getCode()) {
			if (itineraire.getEtat() == CodeEtatItinieraire.EN_COURS.getCode()) {
				itineraire.setEtat(CodeEtatItinieraire.EN_INCIDENT.getCode());
			}
			if (incident.getHeureDebut().plusMinutes(incident.getDuree())
					.isBefore(LocalDateTime.now().plus(ajoutDuree, chronoUnit))) {
				incident.setDuree(incident.getDuree() + ajoutDuree);
				incident.setHeureTheoriqueDeFin(incident.getDuree(), chronoUnit);
			}
			res = true;
		} else if (newEtatIncident == CodeEtatIncident.RESOLU.getCode()) {
			// On remet l'itinéraire à l'état en cours
			itineraire.setEtat(CodeEtatItinieraire.EN_COURS.getCode());
			// Si on a rallongé l'incident de 5min et qu'au bout d'1min il est finalement
			// résolu, on veut raccourcir les retards concernés de 4min
			LocalDateTime now = LocalDateTime.now();
			LocalTime avancementHeureFin = incident.getHeureTheoriqueDeFin().minusHours(now.getHour())
					.minusMinutes(now.getMinute()).minusSeconds(now.getSecond()).minusNanos(now.getNano())
					.toLocalTime();
			this.serviceMajDecideur.decideRetard(new Retard(itineraire, avancementHeureFin));
			res = true;
		}
		return res;
	}

	private LocalTime estimationTempsRetard(int codeTypeIncident) {
		return CodeTypeIncident.getTempEstimation(codeTypeIncident);
	}
}
