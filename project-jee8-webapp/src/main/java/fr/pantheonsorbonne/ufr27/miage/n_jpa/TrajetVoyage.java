package fr.pantheonsorbonne.ufr27.miage.n_jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @ToString
public class TrajetVoyage {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	@OneToOne
	Train train;
	@OneToOne
	Gare gareDepart;
	@OneToOne
	Gare gareArrivee;
	@OneToOne
	Voyage voyage;
	@OneToOne
	Trajet trajet;
	
}
