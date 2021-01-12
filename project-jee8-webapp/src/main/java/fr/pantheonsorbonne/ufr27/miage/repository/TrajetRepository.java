package fr.pantheonsorbonne.ufr27.miage.repository;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;

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
