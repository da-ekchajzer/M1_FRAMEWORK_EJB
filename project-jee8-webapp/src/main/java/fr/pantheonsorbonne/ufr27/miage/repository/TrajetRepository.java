package fr.pantheonsorbonne.ufr27.miage.repository;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;

@ManagedBean
@RequestScoped
public class TrajetRepository {

	@Inject
	TrajetDAO trajetDAO;
	
	/**
	 * Récupérer l'ensemble des trajets qui constituent l'itinéraire passé en paramètre
	 * @param itineraire
	 * @return
	 */
	public List<Trajet> getTrajetsByItineraire(Itineraire itineraire) {
		return trajetDAO.getTrajetsByItineraire(itineraire);
	}	

}
