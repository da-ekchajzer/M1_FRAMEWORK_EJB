package fr.pantheonsorbonne.ufr27.miage.pojos;

import java.time.LocalDateTime;

public class Itineraire {

	int etatItineraire;
	String idItineraire;
	LocalDateTime heureArriveeEnGare;
	LocalDateTime heureDepartDeGare;

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
