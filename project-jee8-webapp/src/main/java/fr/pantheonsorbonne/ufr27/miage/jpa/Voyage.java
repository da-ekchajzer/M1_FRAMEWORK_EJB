package fr.pantheonsorbonne.ufr27.miage.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
		@NamedQuery(name = "Voyage.getVoyagesByGareDeDepart", query = "SELECT v FROM Voyage v, Gare g WHERE v.gareDeDepart.id = g.id AND g.nom = :nom"),
		@NamedQuery(name = "Voyage.getVoyagesByGareArrivee", query = "SELECT v FROM Voyage v, Gare g WHERE v.gareArrivee.id = g.id AND g.nom = :nom"),
		@NamedQuery(name = "Voyage.getAllVoyages", query = "SELECT v FROM Voyage v")

})
public class Voyage {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	// Un Trajet fait partie de plusieurs Voyages
	// Un Voyage est composé de plusieurs Trajets
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	List<Trajet> trajets;

	List<Voyageur> voyageurs;

	@ManyToOne(fetch = FetchType.LAZY)
	Gare gareDeDepart;

	@ManyToOne(fetch = FetchType.LAZY)
	Gare gareArrivee;

	public Voyage() {
	}

	public Voyage(List<Trajet> trajets) {
		this.trajets = trajets;
		this.voyageurs = new ArrayList<Voyageur>();
		this.gareDeDepart = this.trajets.get(0).getGareDepart();
		this.gareArrivee = this.trajets.get(trajets.size() - 1).getGareArrivee();
	}

	public int getId() {
		return id;
	}

	public List<Trajet> getTrajets() {
		return trajets;
	}

	public void setTrajets(List<Trajet> trajets) {
		this.trajets = trajets;
	}

	public List<Voyageur> getVoyageurs() {
		return voyageurs;
	}

	public void setVoyageurs(List<Voyageur> voyageurs) {
		this.voyageurs = voyageurs;
	}
	
	public Gare getGareDeDepart() {
		return gareDeDepart;
	}

	public void setGareDeDepart(Gare gareDeDepart) {
		this.gareDeDepart = gareDeDepart;
	}

	public Gare getGareArrivee() {
		return gareArrivee;
	}

	public void setGareArrivee(Gare gareArrivee) {
		this.gareArrivee = gareArrivee;
	}

	public void addVoyageur(Voyageur v) {
		voyageurs.add(v);
	}
	
}
