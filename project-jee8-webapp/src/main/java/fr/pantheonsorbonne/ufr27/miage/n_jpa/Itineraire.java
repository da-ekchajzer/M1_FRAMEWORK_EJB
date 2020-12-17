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
		@NamedQuery(name = "Itineraire.getItineraireByTrainEtEtat", query = "SELECT i FROM Itineraire i WHERE i.train.id = :idTrain and i.etat = :etat"),
		@NamedQuery(name = "Itineraire.getAllItinerairesByEtat", query = "SELECT i FROM Itineraire i WHERE i.etat = :etat"),
		@NamedQuery(name = "Itineraire.getAllItineraires", query = "SELECT i FROM Itineraire i"),

})
public class Itineraire {

	public Itineraire() {
		this.arretsDesservis = new LinkedList<Arret>();
	}

	public Itineraire(Train train) {
		this.train = train;
		this.arretsDesservis = new LinkedList<Arret>();
	}

	public Itineraire(Train train, List<Arret> arretsDesservis) {
		this.train = train;
		this.arretsDesservis = arretsDesservis;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRAIN_ID")
	Train train;
	List<Voyageur> voyageurs;
	List<Arret> arretsDesservis;
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

	public List<Arret> getArretsDesservis() {
		return arretsDesservis;
	}

	public void setArretsDesservis(List<Arret> arretsDesservis) {
		this.arretsDesservis = arretsDesservis;
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
		this.arretsDesservis.add(a);
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
