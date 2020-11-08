package fr.pantheonsorbonne.ufr27.miage.n_jpa;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @ToString
public class Voyageur {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	String nom;
	String prenom;
	
	// Un voyageur a 1 seul voyage
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="VOYAGE_ID")
	Voyage voyage;
	
	
	
}
