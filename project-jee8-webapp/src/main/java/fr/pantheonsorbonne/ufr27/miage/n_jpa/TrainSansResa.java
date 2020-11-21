package fr.pantheonsorbonne.ufr27.miage.n_jpa;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @ToString
public class TrainSansResa extends Train {
	
	public TrainSansResa() {}
	
	public TrainSansResa(String marque) {
		this.marque = marque;
	}
	
}
