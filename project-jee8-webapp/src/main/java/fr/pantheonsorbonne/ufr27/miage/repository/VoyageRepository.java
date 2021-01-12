package fr.pantheonsorbonne.ufr27.miage.repository;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.dao.VoyageDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.jpa.Voyage;

@ManagedBean
@RequestScoped
public class VoyageRepository {

	@Inject
	VoyageDAO voyageDAO;

	public List<Voyage> getVoyagesByNomGareDepart(Gare gareDepart) {
		return voyageDAO.getVoyagesByNomGareDepart(gareDepart);
	}

	public List<Voyage> getVoyagesByNomGareArrivee(Gare gareArrivee) {
		return voyageDAO.getVoyagesByNomGareArrivee(gareArrivee);
	}

	public List<Voyage> getAllVoyages() {
		return voyageDAO.getAllVoyages();
	}

	/**
	 * On récupére l'ensemble des voyages qui sont composés d'au moins
	 * un trajet parmi la liste des trajets passés en paramètre
	 * @param trajets
	 * @return
	 */
	public List<Voyage> getVoyagesComposedByAtLeastOneTrajetOf(List<Trajet> trajets) {
		List<Voyage> allVoyages = this.getAllVoyages();
		List<Voyage> resVoyages = new ArrayList<Voyage>();

		for (Trajet t : trajets) {
			for (Voyage v : allVoyages) {
				if (v.getTrajets().contains(t)) {
					if(!resVoyages.contains(v)) resVoyages.add(v);
				}
			}
		}

		return resVoyages;
	}
	
}
