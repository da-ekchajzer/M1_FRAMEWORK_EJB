package fr.pantheonsorbonne.ufr27.miage.n_jpa;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @ToString
public class Itineraire {

	public Itineraire() {}
	
	public Itineraire(Train train, List<Voyageur> voyageurs, List<Arret> garesDesservies) {
		
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="TRAIN_ID")
	Train train;
	List<Voyageur> voyageurs;
	List<Arret> garesDesservies;
	int etat;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="INCIDENT_ID")
	private Incident incident;

	public int getId() {
		return id;
	}

	public Train getTrain() {
		return train;
	}

	public void setTrain(Train train) {
		this.train = train;
	}

	public List<Voyageur> getVoyageurs() {
		return voyageurs;
	}

	public void setVoyageurs(List<Voyageur> voyageurs) {
		this.voyageurs = voyageurs;
	}

	public List<Arret> getGaresDesservies() {
		return garesDesservies;
	}
	
	public void setGaresDesservies(List<Arret> garesDesservies) {
		this.garesDesservies = garesDesservies;
	}

	public int getEtat() {
		return etat;
	}

	public void setEtat(int etat) {
		this.etat = etat;
	}

	public Incident getIncident() {
		return incident;
	}

	public void setIncident(Incident incident) {
		this.incident = incident;
	}
	
	public void addGare(Gare gare, Date heureArriveeEnGare, Date heureDepartDeGare) {
		this.garesDesservies.add(new Arret(gare, heureArriveeEnGare, heureDepartDeGare));
	}
	
	@Entity
	public static class Arret {
		
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		int id;
		
		Gare gareArret;
		Date heureArriveeEnGare;
		Date heureDepartDeGare;
		
		public Arret() {}
		
		public Arret(Gare gare, Date heureArriveeEnGare, Date heureDepartDeGare) {
			this.gareArret = gare;
			this.heureArriveeEnGare = heureArriveeEnGare;
			this.heureDepartDeGare = heureDepartDeGare;
		}
		
	}
	
	
	
	
	
}
