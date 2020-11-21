package fr.pantheonsorbonne.ufr27.miage.n_jpa;

import java.time.LocalDateTime;

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
	LocalDateTime heureArriveeEnGare;
	LocalDateTime heureDepartDeGare;

	public Arret() {
	}

	public Arret(Gare gare, LocalDateTime heureArriveeEnGare, LocalDateTime heureDepartDeGare) {
		this.gareArret = gare;
		this.heureArriveeEnGare = heureArriveeEnGare;
		this.heureDepartDeGare = heureDepartDeGare;
	}
}
