package fr.pantheonsorbonne.ufr27.miage.jpa;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class TrainSansResa extends Train {

	public TrainSansResa() {
		super();
	}

	public TrainSansResa(String marque) {
		super(marque);
		this.marque = marque;
	}

}
