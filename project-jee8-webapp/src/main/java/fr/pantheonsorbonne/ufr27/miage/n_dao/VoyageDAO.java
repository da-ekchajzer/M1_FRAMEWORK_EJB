package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyage;

@ManagedBean
public class VoyageDAO {

	@Inject
	EntityManager em;

	// On cherche par le nom de la gare ou par une gare carrément ?
	public List<Voyage> getVoyagesByNomGareDepart(Gare gareDepart) {
		return (List<Voyage>) em.createNamedQuery("Voyage.getVoyagesByGareDeDepart", Voyage.class)
				.setParameter("nom", gareDepart.getNom()).getResultList();
	}

	// On cherche par le nom de la gare ou par une gare carrément ?
	public List<Voyage> getVoyagesByNomGareArrivee(Gare gareArrivee) {
		return (List<Voyage>) em.createNamedQuery("Voyage.getVoyagesByGareArrivee", Voyage.class)
				.setParameter("nom", gareArrivee.getNom()).getResultList();
	}
	
	/**
	 * @author Mathieu
	 * 26/11/2020 (Matin)
	 * 
	 */
	public List<Voyage> getAllVoyages() {
		return (List<Voyage>) em.createNamedQuery("Voyage.getAllVoyages", Voyage.class).getResultList();
	}
	
	/**
	 * @author Mathieu
	 * 26/11/2020 (Matin)
	 * 
	 * On vérifie pour chaque voyage s'il est composé d'un des trajets de la liste
	 * passées en paramètre
	 * Si oui, on le met dans la liste des voyages qu'on renvoie
	 * @param trajets
	 * @return
	 */
	public List<Voyage> getVoyagesComposesByUnTrajet(List<Trajet> trajets) {
		List<Voyage> allVoyages = this.getAllVoyages();
		List<Voyage> resVoyages = new ArrayList<Voyage>();
		
		for(Trajet t : trajets) {
			for(Voyage v : allVoyages) {
				if(v.getTrajets().contains(t)) resVoyages.add(v); 
			}
		}
		return resVoyages;
	}
}
