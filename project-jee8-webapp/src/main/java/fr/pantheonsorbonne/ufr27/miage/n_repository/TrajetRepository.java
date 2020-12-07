package fr.pantheonsorbonne.ufr27.miage.n_repository;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.n_dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyage;

@ManagedBean
@RequestScoped
public class TrajetRepository {

	@Inject
	TrajetDAO trajetDAO;
	
	public List<Trajet> getTrajetsByItineraire(Itineraire itineraire) {
		return trajetDAO.getTrajetsByItineraire(itineraire);
	}

	public List<Trajet> getTrajetsByNomGareDeDepart(Gare gareDepart) {
		return trajetDAO.getTrajetsByNomGareDeDepart(gareDepart);
	}
	
	public List<Trajet> getTrajetsByNomGareArrivee(Gare gareArrivee) {
		return trajetDAO.getTrajetsByNomGareArrivee(gareArrivee);
	}

	public void deleteTrajet(Trajet trajet) {
		trajetDAO.deleteTrajet(trajet);
	}
	
}
