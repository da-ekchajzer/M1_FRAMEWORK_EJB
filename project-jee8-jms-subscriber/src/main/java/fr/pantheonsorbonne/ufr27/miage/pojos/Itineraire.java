package fr.pantheonsorbonne.ufr27.miage.pojos;

import java.time.LocalDateTime;
import java.util.List;

public class Itineraire {

	int etatItineraire;
	String idItineraire;
	String gareDepart;
	String gareArrive;
	List<String> gareDesservis;
	LocalDateTime heureArriveeEnGare;
	LocalDateTime heureDepartDeGare;
	
	public List<String> getGareDesservis() {
		return gareDesservis;
	}

	public void setGareDesservis(List<String> gareDesservis) {
		this.gareDesservis = gareDesservis;
	}

	public String getGareDepart() {
		return gareDepart;
	}

	public void setGareDepart(String gareDepart) {
		this.gareDepart = gareDepart;
	}
	
	public String getGareArrive() {
		return gareArrive;
	}

	public void setGareArrive(String gareArrive) {
		this.gareArrive = gareArrive;
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
