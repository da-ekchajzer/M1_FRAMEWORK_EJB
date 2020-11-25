package fr.pantheonsorbonne.ufr27.miage.n_jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Voyage {

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	// Un Trajet fait partie de plusieurs Voyages
	// Un Voyage est composé de plusieurs Trajets
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	List<Trajet> trajets;

	List<Voyageur> voyageurs;
	
	public Voyage() {}
	
	public Voyage(List<Trajet> trajets) {
		this.trajets = trajets;
		this.voyageurs = new ArrayList<Voyageur>();
	}
	
	public void addVoyageur(Voyageur v) {
		voyageurs.add(v);
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

	public int getId() {
		return id;
	}
	
}
