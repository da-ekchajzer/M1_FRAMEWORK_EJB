package fr.pantheonsorbonne.ufr27.miage.n_jpa;

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
@NamedQueries({
	@NamedQuery(name = "Train.getTrainById", query = "SELECT t FROM Train t WHERE t.id = :id")

})
public abstract class Train {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	public int getId() {
		return id;
	}

	String marque;

	public String getMarque() {
		return marque;
	}

	public void setMarque(String marque) {
		this.marque = marque;
	}

}
