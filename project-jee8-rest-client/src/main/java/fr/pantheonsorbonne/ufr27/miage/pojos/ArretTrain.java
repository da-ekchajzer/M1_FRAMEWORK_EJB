package fr.pantheonsorbonne.ufr27.miage.pojos;

import java.time.LocalDateTime;

public class ArretTrain {

	String nomGare;
	LocalDateTime heureArrive;
	LocalDateTime heureDepart;
	
	public String getNomGare() {
		return nomGare;
	}
	public void setNomGare(String nomGare) {
		this.nomGare = nomGare;
	}
	public LocalDateTime getHeureDepart() {
		return heureDepart;
	}
	public void setHeureDepart(LocalDateTime heureDepart) {
		this.heureDepart = heureDepart;
	}
	public LocalDateTime getheureArrive() {
		return heureArrive;
	}
	public void setheureArrive(LocalDateTime heureArrive) {
		this.heureArrive = heureArrive;
	}
}