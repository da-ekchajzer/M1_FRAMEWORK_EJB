package fr.pantheonsorbonne.ufr27.miage.jpa;

import javax.persistence.Entity;

@Entity
public class TrainSansResa extends Train {

	public TrainSansResa() {
		super();
	}

	public TrainSansResa(String marque) {
		super(marque);
		this.marque = marque;
	}
	
	public TrainSansResa(String marque, String businessId) {
		super(marque, businessId);
		//this.marque = marque;
	}

}
