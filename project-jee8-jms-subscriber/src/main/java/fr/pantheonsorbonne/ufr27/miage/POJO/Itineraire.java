package fr.pantheonsorbonne.ufr27.miage.POJO;

import java.time.LocalDateTime;
import java.util.List;

public class Itineraire {

	int etatItineraire;
	String idItineraire;
	List<String> garesDeservis;
	LocalDateTime heureArriveeEnGare;
	LocalDateTime heureDepartDeGare;

	public List<String> getGaresDeservis() {
		return garesDeservis;
	}

	public void setGaresDeservis(List<String> garesDeservis) {
		this.garesDeservis = garesDeservis;
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
