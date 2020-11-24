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

	Gare gare;
	LocalDateTime heureArriveeEnGare;
	LocalDateTime heureDepartDeGare;

	public Arret() {
	}

	public Arret(Gare gare, LocalDateTime heureArriveeEnGare, LocalDateTime heureDepartDeGare) {
		this.gare = gare;
		this.heureArriveeEnGare = heureArriveeEnGare;
		this.heureDepartDeGare = heureDepartDeGare;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Gare getGare() {
		return gare;
	}

	public void setGare(Gare gare) {
		this.gare = gare;
	}

	public LocalDateTime getHeureArriveeEnGare() {
		return heureArriveeEnGare;
	}

	public void setHeureArriveeEnGare(LocalDateTime heureArriveeEnGare) {
		this.heureArriveeEnGare = heureArriveeEnGare;
	}

	public LocalDateTime getHeureDepartDeGare() {
		return heureDepartDeGare;
	}

	public void setHeureDepartDeGare(LocalDateTime heureDepartDeGare) {
		this.heureDepartDeGare = heureDepartDeGare;
	}
}
