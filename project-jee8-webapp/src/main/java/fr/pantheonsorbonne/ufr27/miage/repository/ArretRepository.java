package fr.pantheonsorbonne.ufr27.miage.repository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.dao.ArretDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;

@RequestScoped
public class ArretRepository {

	@Inject
	ArretDAO arretDAO;

	public void supprimerArret(Arret arret) {
		arretDAO.supprimerArret(arret);
	}

	public void avancerHeureArriveeEnGare(Arret a, int tempsAvance) {
		if (a.getHeureArriveeEnGare() != null) {
			arretDAO.avancerHeureArriveeEnGare(a, tempsAvance);
		}
	}

	public void avancerHeureHeureDepartDeGare(Arret a, int tempsAvance) {
		if (a.getHeureDepartDeGare() != null) {
			arretDAO.avancerHeureDepartDeGare(a, tempsAvance);
		}
	}

	public void retarderHeureArriveeEnGare(Arret a, int tempsRetard) {
		if (a.getHeureArriveeEnGare() != null) {
			arretDAO.retarderHeureArriveeEnGare(a, tempsRetard);
		}
	}

	public void retarderHeureDepartDeGare(Arret a, int tempsRetard) {
		if (a.getHeureDepartDeGare() != null) {
			arretDAO.retardHeureDepartDeGare(a, tempsRetard);
		}
	}

	public Arret getArretParItineraireEtNomGare(Itineraire itineraire, String nomGare) {
		Arret arret = null;
		for (Arret a : itineraire.getArretsDesservis()) {
			if (a.getGare().getNom().equals(nomGare)) {
				arret = a;
				break;
			}
		}
		return arret;
	}
}
