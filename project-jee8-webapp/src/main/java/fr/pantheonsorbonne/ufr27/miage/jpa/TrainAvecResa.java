package fr.pantheonsorbonne.ufr27.miage.jpa;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;

@Entity
public class TrainAvecResa extends Train {

	Set<Voyageur> voyageurs;

	public TrainAvecResa() {
		super();
	}

	public TrainAvecResa(String marque) {
		super(marque);
		this.voyageurs = new HashSet<Voyageur>();
	}

	public Set<Voyageur> getVoyageurs() {
		return voyageurs;
	}

	public void setVoyageurs(Set<Voyageur> voyageursAyantReserves) {
		this.voyageurs = voyageursAyantReserves;
	}

	public void addVoyageur(Voyageur voyageur) {
		this.voyageurs.add(voyageur);
	}

	public void removeVoyageur(Voyageur voyageur) {
		this.voyageurs.remove(voyageur);
	}
	
}
