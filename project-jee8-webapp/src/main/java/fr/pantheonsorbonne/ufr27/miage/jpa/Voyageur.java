package fr.pantheonsorbonne.ufr27.miage.jpa;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
		@NamedQuery(name = "Voyageur.getVoyageursByVoyageActuel", query = "SELECT v FROM Voyageur v WHERE v.voyageActuel.id = :id"),
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

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "VOYAGE_ID")
	List<Voyage> voyages;
	
	Voyage voyageActuel;

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

	public List<Voyage> getVoyages() {
		return voyages;
	}

	public void setVoyages(List<Voyage> voyages) {
		this.voyages = voyages;
	}
	
	public void addVoyage(Voyage voyage) {
		this.voyages.add(voyage);
	}

	public Voyage getVoyageActuel() {
		return voyageActuel;
	}

	public void setVoyageActuel(Voyage voyageActuel) {
		this.voyageActuel = voyageActuel;
	}

}
