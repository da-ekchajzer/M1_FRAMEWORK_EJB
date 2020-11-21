package fr.pantheonsorbonne.ufr27.miage.n_jpa;

import java.util.List;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @ToString
public class TrainAvecResa extends Train {
	
	List<Voyageur> voyageursAyantReserves;
	
	public TrainAvecResa() {}
	
	public TrainAvecResa(List<Voyageur> voyageursAyantReserves, String marque) {
		this.voyageursAyantReserves = voyageursAyantReserves;
		this.marque = marque;
	}
	
}
