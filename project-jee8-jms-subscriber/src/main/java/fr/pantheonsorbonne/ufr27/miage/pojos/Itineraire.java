package fr.pantheonsorbonne.ufr27.miage.pojos;

import java.time.LocalDateTime;
import java.util.List;

public class Itineraire {

	int etatItineraire;
	String idItineraire;
	String gareDepart;
	String gareArrivee;
	List<String> garesDesservies;
	LocalDateTime heureArriveeEnGare;
	LocalDateTime heureDepartDeGare;
	
	public List<String> getGaresDesservies() {
		return garesDesservies;
	}

	public void setGaresDesservies(List<String> gareDesservis) {
		this.garesDesservies = gareDesservis;
	}

	public String getGareDepart() {
		return gareDepart;
	}

	public void setGareDepart(String gareDepart) {
		this.gareDepart = gareDepart;
	}
	
	public String getGareArrivee() {
		return gareArrivee;
	}

	public void setGareArrivee(String gareArrive) {
		this.gareArrivee = gareArrive;
	}
	
	public int getEtatItineraire() {
		return etatItineraire;
	}

	public void setEtatItineraire(int etatItineraire) {
		this.etatItineraire = etatItineraire;
	}

	public String getIdItineraire() {
		return idItineraire;
	}

	public void setIdItineraire(String idItineraire) {
		this.idItineraire = idItineraire;
	}

	public LocalDateTime getHeureArriveeEnGare() {
		return heureArriveeEnGare;
	}

	public void setHeureArriveeEnGare(LocalDateTime heureArriveeEnGare) {
		this.heureArriveeEnGare = heureArriveeEnGare;
	}

	public LocalDateTime getHeureDepartDeGare() {
		return heureDepartDeGare;
	}

	public void setHeureDepartDeGare(LocalDateTime heureDepartDeGare) {
		this.heureDepartDeGare = heureDepartDeGare;
	}

}
