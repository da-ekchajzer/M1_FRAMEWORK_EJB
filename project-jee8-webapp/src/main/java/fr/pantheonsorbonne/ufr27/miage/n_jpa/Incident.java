package fr.pantheonsorbonne.ufr27.miage.n_jpa;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @ToString
@NamedQueries({
	@NamedQuery(name="Incident.getAllIncidents", query="SELECT i FROM Incident i"),
	@NamedQuery(name="Incident.getNbIncidents", query="SELECT COUNT(i) FROM Incident i"),
	@NamedQuery(name="Incident.getIncidentById", query="SELECT i FROM Incident i WHERE i.id = :id")
})
public class Incident {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	int typeIncident;
	LocalDateTime heureDebut;
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

	public int getTypeIncident() {
		return typeIncident;
	}

	public void setTypeIncident(int typeIncident) {
		this.typeIncident = typeIncident;
	}

	public LocalDateTime getHeureDebut() {
		return heureDebut;
	}

	public void setHeureDebut(LocalDateTime heureDebut) {
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
