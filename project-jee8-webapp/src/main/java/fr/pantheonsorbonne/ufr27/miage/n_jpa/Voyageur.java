package fr.pantheonsorbonne.ufr27.miage.n_jpa;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Voyageur {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	String nom;
	String prenom;

	public Voyageur() {
	}

	public Voyageur(String prenom, String nom) {
		this.prenom = prenom;
		this.nom = nom;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VOYAGE_ID")
	Voyage voyage;

	public int getId() {
		return id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public Voyage getVoyage() {
		return voyage;
	}

	public void setVoyage(Voyage voyage) {
		this.voyage = voyage;
	}

}
