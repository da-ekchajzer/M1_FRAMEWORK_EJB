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

	/**
	 * Supprimer un arrêt
	 * @param arret
	 */
	public void supprimerArret(Arret arret) {
		arretDAO.supprimerArret(arret);
	}

	/**
	 * Avancer l'heure d'arrivée en gare de tempsAvance heure(s)/minute(s)/seconde(s)
	 * pour l'arrêt passé en paramètre
	 * @param a
	 * @param tempsAvance
	 */
	public void avancerHeureArriveeEnGare(Arret a, int tempsAvance) {
		if (a.getHeureArriveeEnGare() != null) {
			arretDAO.avancerHeureArriveeEnGare(a, tempsAvance);
		}
	}

	/**
	 * Avancer l'heure de départ de gare de tempsAvance heure(s)/minute(s)/seconde(s)
	 * pour l'arrêt passé en paramètre
	 * @param a
	 * @param tempsAvance
	 */
	public void avancerHeureHeureDepartDeGare(Arret a, int tempsAvance) {
		if (a.getHeureDepartDeGare() != null) {
			arretDAO.avancerHeureDepartDeGare(a, tempsAvance);
		}
	}

	/**
	 * Reculer l'heure d'arrivée en gare de tempsAvance heure(s)/minute(s)/seconde(s)
	 * pour l'arrêt passé en paramètre
	 * @param a
	 * @param tempsAvance
	 */
	public void retarderHeureArriveeEnGare(Arret a, int tempsRetard) {
		if (a.getHeureArriveeEnGare() != null) {
			arretDAO.retarderHeureArriveeEnGare(a, tempsRetard);
		}
	}

	/**
	 * Reculer l'heure de départ de gare de tempsAvance heure(s)/minute(s)/seconde(s)
	 * pour l'arrêt passé en paramètre
	 * @param a
	 * @param tempsAvance
	 */
	public void retarderHeureDepartDeGare(Arret a, int tempsRetard) {
		if (a.getHeureDepartDeGare() != null) {
			arretDAO.retardHeureDepartDeGare(a, tempsRetard);
		}
	}

	/**
	 * Récupérer un arrêt faisant partie de l'itinéraire passé en paramètre
	 * et ayant pour nom de gare nomGare
	 * @param itineraire
	 * @param nomGare
	 * @return
	 */
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
