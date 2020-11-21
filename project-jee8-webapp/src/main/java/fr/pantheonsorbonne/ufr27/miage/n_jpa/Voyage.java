package fr.pantheonsorbonne.ufr27.miage.n_jpa;

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

	Gare gareDepart;
	Gare gareArrivee;

	// Un Trajet fait partie de plusieurs Voyages
	// Un Voyage est composé de plusieurs Trajets
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	List<Trajet> trajets;

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
