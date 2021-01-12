package fr.pantheonsorbonne.ufr27.miage.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NamedQueries({ @NamedQuery(name = "Train.getTrainById", query = "SELECT t FROM Train t WHERE t.id = :id"),
		@NamedQuery(name = "Train.getTrainByBusinessId", query = "SELECT t FROM Train t WHERE t.businessId = :id"),
		@NamedQuery(name = "Train.getAllTrains", query = "SELECT t FROM Train t")

})
public abstract class Train {

	public static int businessIdTrainCount = 1;

	public Train() {
		this.marque = "not mentioned";
	}

	public Train(String marque) {
		this.businessId = "T" + businessIdTrainCount++;
		this.marque = marque;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	String businessId;

	String marque;

	public String getMarque() {
		return marque;
	}

	public void setMarque(String marque) {
		this.marque = marque;
	}

	public int getId() {
		return id;
	}

	public String getBusinessId() {
		return businessId;
	}

}
