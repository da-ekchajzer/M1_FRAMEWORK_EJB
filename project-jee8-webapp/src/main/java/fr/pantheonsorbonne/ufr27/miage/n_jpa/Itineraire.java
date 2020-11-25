package fr.pantheonsorbonne.ufr27.miage.n_jpa;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NamedQueries({
		@NamedQuery(name = "Itineraire.getItineraireById", query = "SELECT i FROM Itineraire i WHERE i.id = :id"),
		@NamedQuery(name = "Itineraire.getNbArretsByItineraire", query = "SELECT COUNT(i) FROM Itineraire i WHERE i.id = :id"),
		@NamedQuery(name = "Itineraire.getAllArretsByItineraire", query = "SELECT i FROM Itineraire i WHERE i.id = :id"),
		@NamedQuery(name = "Itineraire.getItineraireByTrainEtEtat", query = "SELECT i FROM Itineraire i WHERE i.train.id = :idTrain and i.etat = :etat") })
public class Itineraire {

	public Itineraire() {
	}

	public Itineraire(Train train) {
		this.train = train;
		this.garesDesservies = new LinkedList<Arret>();
	}

	public Itineraire(Train train, List<Arret> garesDesservies) {
		this.train = train;
		this.garesDesservies = garesDesservies;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRAIN_ID")
	Train train;
	List<Voyageur> voyageurs;
	List<Arret> garesDesservies;
	int etat;

	Arret arretActuel;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INCIDENT_ID")
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
	

	public Arret getArretActuel() {
		return arretActuel;
	}

	public void setArretActuel(Arret arretActuel) {
		this.arretActuel = arretActuel;
	}

	public void addArret(Arret a) {
		this.garesDesservies.add(a);
	}
	
	public enum CodeEtatItinieraire {
		
		EN_ATTENTE(0), EN_COURS(1), EN_INCIDENT(2), FIN(-1);
		
		private int code;
		
		private CodeEtatItinieraire(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}
	}
	
}
