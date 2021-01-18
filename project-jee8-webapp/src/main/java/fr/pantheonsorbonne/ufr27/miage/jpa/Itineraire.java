package fr.pantheonsorbonne.ufr27.miage.jpa;

import java.util.ArrayList;
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

@Entity
@NamedQueries({
		@NamedQuery(name = "Itineraire.getItineraireById", query = "SELECT i FROM Itineraire i WHERE i.id = :id"),
		@NamedQuery(name = "Itineraire.getItineraireByBusinessId", query = "SELECT i FROM Itineraire i WHERE i.businessId = :id"),
		@NamedQuery(name = "Itineraire.getItineraireByTrainEtEtat", query = "SELECT i FROM Itineraire i WHERE i.train.id = :idTrain and i.etat = :etat"),
		@NamedQuery(name = "Itineraire.getAllItinerairesByEtat", query = "SELECT i FROM Itineraire i WHERE i.etat = :etat"),
		@NamedQuery(name = "Itineraire.getAllItineraires", query = "SELECT i FROM Itineraire i")

})
public class Itineraire {

	public static int businessIdItineraireCount = 1;

	public Itineraire() {
		this.voyageurs = new ArrayList<Voyageur>();
		this.arretsDesservis = new LinkedList<Arret>();
	}

	public Itineraire(Train train) {
		this();
		this.businessId = "IT" + businessIdItineraireCount++;
		this.train = train;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	String businessId;

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

	public void addVoyageur(Voyageur voyageur) {
		this.voyageurs.add(voyageur);
	}

	public void removeVoyageur(Voyageur voyageur) {
		this.voyageurs.remove(voyageur);
	}

	public List<Arret> getArretsDesservis() {
		return arretsDesservis;
	}

	public void setArretsDesservis(List<Arret> arretsDesservis) {
		this.arretsDesservis = arretsDesservis;
		if (this.arretActuel == null) {
			this.arretActuel = this.arretsDesservis.get(0);
		}
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
		if (this.arretActuel == null) {
			this.arretActuel = a;
		}
	}

	public String getBusinessId() {
		return businessId;
	}

	public Arret getNextArret() {
		Arret result = null;
		for (Arret arret : this.arretsDesservis) {
			if (arret.isAfter(this.arretActuel)) {
				result = arret;
			}
		}
		return result;
	}

	public boolean isGareDesservie(Gare gare) {
		boolean result = false;
		for (Arret a : arretsDesservis) {
			if (a.getGare().equals(gare)) {
				result = true;
				break;
			}
		}
		return result;
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
