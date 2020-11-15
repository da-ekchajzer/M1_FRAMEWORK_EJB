package fr.pantheonsorbonne.ufr27.miage.n_jpa;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Arret {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	Gare gareArret;
	Date heureArriveeEnGare;
	Date heureDepartDeGare;

	public Arret() {
	}

	public Arret(Gare gare, Date heureArriveeEnGare, Date heureDepartDeGare) {
		this.gareArret = gare;
		this.heureArriveeEnGare = heureArriveeEnGare;
		this.heureDepartDeGare = heureDepartDeGare;
	}
}
