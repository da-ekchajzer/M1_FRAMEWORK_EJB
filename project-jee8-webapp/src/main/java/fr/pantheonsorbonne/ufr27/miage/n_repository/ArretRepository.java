package fr.pantheonsorbonne.ufr27.miage.n_repository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.n_dao.ArretDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;

@RequestScoped
public class ArretRepository {
	
	@Inject
	ArretDAO arretDAO;
	
	public void supprimerArret(Arret arret) {
		this.arretDAO.supprimerArret(arret);
	}
	
	public void avancerHeureArriveeEnGare(Arret a, int tempsAvance) {
		this.arretDAO.avancerHeureArriveeEnGare(a, tempsAvance);
	}
	
	public void retarderHeureArriveeEnGare(Arret a, int tempsRetard) {
		this.arretDAO.retarderHeureArriveeEnGare(a, tempsRetard);
	}
	
	public void retardHeureDepartDeGare(Arret a, int tempsRetard) {
		this.arretDAO.retardHeureDepartDeGare(a, tempsRetard);
	}
	
	public Arret getArretParItineraireEtNomGare(Itineraire itineraire, String nomGare) {
		for(Arret a : itineraire.getArretsDesservis()) {
			if(a.getGare().getNom().equals(nomGare)) return a;
		}
		return null;
	}
}
