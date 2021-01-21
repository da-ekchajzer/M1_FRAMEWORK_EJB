package fr.pantheonsorbonne.ufr27.miage.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
		@NamedQuery(name = "Voyageur.getVoyageursByVoyageActuel", query = "SELECT v FROM Voyageur v WHERE v.voyage.id = :id"),
		@NamedQuery(name = "Voyageur.getAllVoyageurs", query = "SELECT v FROM Voyageur v")

})
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

	public void setVoyage(Voyage voyageActuel) {
		this.voyage = voyageActuel;
	}

}
