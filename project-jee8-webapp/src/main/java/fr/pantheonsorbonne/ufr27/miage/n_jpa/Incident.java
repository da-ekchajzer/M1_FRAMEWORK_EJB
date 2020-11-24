package fr.pantheonsorbonne.ufr27.miage.n_jpa;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @ToString
public class Incident {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	String typeIncident;
	Date heureDebut;
	int duree;
	int etat;
	
	// TODO
	public String getStrEtat() {
		String strEtat = "";
		if(etat == 0) strEtat = "en cours";
		else if(etat == 1) strEtat = "terminé";
		else strEtat = "???";
		return strEtat;
	}

	public int getId() {
		return id;
	}

	public String getTypeIncident() {
		return typeIncident;
	}

	public void setTypeIncident(String typeIncident) {
		this.typeIncident = typeIncident;
	}

	public Date getHeureDebut() {
		return heureDebut;
	}

	public void setHeureDebut(Date heureDebut) {
		this.heureDebut = heureDebut;
	}

	public int getDuree() {
		return duree;
	}

	public void setDuree(int duree) {
		this.duree = duree;
	}

	public int getEtat() {
		return etat;
	}

	public void setEtat(int etat) {
		this.etat = etat;
	}
	
	
	
}
