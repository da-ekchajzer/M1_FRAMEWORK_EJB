package fr.pantheonsorbonne.ufr27.miage.dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;

@ManagedBean
@RequestScoped
public class TrajetDAO {

	@Inject
	EntityManager em;

	/**
	 * Récupérer en BD tous les trajets qui constituent l'itinéraire passé en paramètre
	 * @param itineraire
	 * @return
	 */
	public List<Trajet> getTrajetsByItineraire(Itineraire itineraire) {
		return (List<Trajet>) em.createNamedQuery("Trajet.getTrajetsByItineraire", Trajet.class)
				.setParameter("idItineraire", itineraire.getId()).getResultList();
	}
	
	/**
	 * Récupérer en BD l'ensemble des trajets
	 * @return
	 */
	public List<Trajet> getAllTrajets() {
		return (List<Trajet>) em.createNamedQuery("Trajet.getAllTrajets", Trajet.class).getResultList();
	}
	
	
}
