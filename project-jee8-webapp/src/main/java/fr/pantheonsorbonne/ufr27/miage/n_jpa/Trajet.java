package fr.pantheonsorbonne.ufr27.miage.n_jpa;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @ToString
public class Trajet {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;
	
	@OneToOne
	Gare gareDepart;
	@OneToOne
	Gare gareArrivee;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ITINERAIRE_ID")
	Itineraire itineraire;
	
	public Trajet() {}
	
	public Trajet(Gare gareDepart, Gare gareArrivee, Itineraire itineraire) {
		this.gareDepart = gareDepart;
		this.gareArrivee = gareArrivee;
		this.itineraire = itineraire;
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
	
}
