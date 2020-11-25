package fr.pantheonsorbonne.ufr27.miage.n_jpa;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class TrainAvecResa extends Train {

	List<Voyageur> voyageurs;

	public TrainAvecResa() {
	}

	public TrainAvecResa(String marque) {
		this.voyageurs = new ArrayList<Voyageur>();
		this.marque = marque;
	}

	public List<Voyageur> getVoyageurs() {
		return voyageurs;
	}

	public void setVoyageurs(List<Voyageur> voyageursAyantReserves) {
		this.voyageurs = voyageursAyantReserves;
	}

	public void addVoyageurInTrain(Voyageur voyageur) {
		this.voyageurs.add(voyageur);
	}
}
