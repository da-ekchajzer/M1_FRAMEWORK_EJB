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
@NamedQueries({
	@NamedQuery(name = "Gare.getGaresByNom", query = "SELECT g FROM Gare g WHERE g.nom = :nom")

})
public class Gare {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	String nom;

	public Gare() {
	}

	public Gare(String nom) {
		this.nom = nom;
	}

	public int getId() {
		return id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

}
