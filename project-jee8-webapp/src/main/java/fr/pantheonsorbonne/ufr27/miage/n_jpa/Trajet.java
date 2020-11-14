package fr.pantheonsorbonne.ufr27.miage.n_jpa;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @ToString
public class Trajet {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;
	
	// TODO : quelle différence entre un Trajet et un Voyage ?

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="TRAIN_ID")
	Train train;
	List<Voyageur> voyageurs;	//Doit-pas avoir une liaison 1-n entre Trajet et Voyageur sur le schéma ?
	HashMap<Gare, Date[]> gares_desservies;
	int nb_personne;	// = voyageurs.size ??
	int etat;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="INCIDENT_ID")
	private Incident incident;
	
	
}
