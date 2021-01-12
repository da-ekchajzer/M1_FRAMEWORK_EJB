package fr.pantheonsorbonne.ufr27.miage.service.utils;

import java.time.LocalTime;

import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;

public class Retard {

	Itineraire itineraire;

	LocalTime tempsDeRetard;

	public Retard(Itineraire it, LocalTime tempsDeRetard) {
		this.itineraire = it;
		this.tempsDeRetard = tempsDeRetard;
	}

	public Itineraire getItineraire() {
		return itineraire;
	}

	public void setItineraire(Itineraire it) {
		this.itineraire = it;
	}

	public LocalTime getTempsDeRetard() {
		return tempsDeRetard;
	}

	public void setTempsDeRetard(LocalTime tempsDeRetard) {
		this.tempsDeRetard = tempsDeRetard;
	}

}
