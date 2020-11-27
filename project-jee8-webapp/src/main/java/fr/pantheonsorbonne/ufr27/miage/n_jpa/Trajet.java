package fr.pantheonsorbonne.ufr27.miage.n_jpa;

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
		@NamedQuery(name = "Trajet.getTrajetsByItineraire", query = "SELECT t FROM Trajet t, Itineraire i WHERE t.itineraire.id = :idItineraire"),
		@NamedQuery(name = "Trajet.getTrajetsByNomGareDeDepart", query = "SELECT t FROM Trajet t, Gare g WHERE g.nom = :nom"),
		@NamedQuery(name = "Trajet.getTrajetsByNomGareArrivee", query = "SELECT t FROM Trajet t, Gare g WHERE g.nom = :nom")

})
public class Trajet implements Comparable<Trajet> {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	@ManyToOne(fetch = FetchType.LAZY)
	Gare gareDepart;
	@ManyToOne(fetch = FetchType.LAZY)
	Gare gareArrivee;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ITINERAIRE_ID")
	Itineraire itineraire;
	
	int numero;

	public Trajet() {
	}

	public Trajet(Gare gareDepart, Gare gareArrivee, Itineraire itineraire, int numero) {
		this.gareDepart = gareDepart;
		this.gareArrivee = gareArrivee;
		this.itineraire = itineraire;
		this.numero = numero;
	}

	public int getId() {
		return id;
	}

	public Gare getGareDepart() {
		return gareDepart;
	}

	public void setGareDepart(Gare gareDepart) {
		this.gareDepart = gareDepart;
	}

	public Gare getGareArrivee() {
		return gareArrivee;
	}

	public void setGareArrivee(Gare gareArrivee) {
		this.gareArrivee = gareArrivee;
	}

	public Itineraire getItineraire() {
		return itineraire;
	}

	public void setItineraire(Itineraire itineraire) {
		this.itineraire = itineraire;
	}
	
	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	@Override
	public int compareTo(Trajet trajet2) {
		// TODO Auto-generated method stub
		if (this.numero > trajet2.getNumero()) {
			return 1;
		}
		else if (this.numero < trajet2.getNumero()) {
			return -1;
		}
		return 0;
	}

}
