package fr.pantheonsorbonne.ufr27.miage.jpa;

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
public class Arret implements Comparable<Arret> {

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

	@Override
	public int compareTo(Arret arret2) {
		int res = 0;
		// Si this.heureDepartDeGare est null, arret2.heureDepartDeGare est forcément !=
		// null (et vice versa)
		if (this.heureDepartDeGare == arret2.getHeureDepartDeGare()) {
			res = 0;
		} else if (this.heureDepartDeGare == null) {
			res = 1;
		} else if (arret2.getHeureDepartDeGare() == null) {
			res = -1;
		} else {
			if (this.heureDepartDeGare.isAfter(arret2.getHeureDepartDeGare())) {
				res = 1;
			} else if (this.heureDepartDeGare.isBefore(arret2.getHeureDepartDeGare())) {
				res = -1;
			}
		}
		return res;
	}

	public boolean isBefore(Arret arret2) {
		if (this.compareTo(arret2) >= 0) {
			return false;
		}
		return true;
	}

	public boolean isAfter(Arret arret2) {
		if (this.compareTo(arret2) <= 0) {
			return false;
		}
		return true;
	}

}
