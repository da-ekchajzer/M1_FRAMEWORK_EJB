package fr.pantheonsorbonne.ufr27.miage.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.jpa.Voyageur;

@ManagedBean
@RequestScoped
public class VoyageurDAO {

	@Inject
	EntityManager em;

	/**
	 * Ajouter/Supprimer les voyageurs du train AVEC RESA passé en paramètre et de
	 * l'itinéraire passé en paramètre en fonction des étapes de leur voyage (gares
	 * de départ/d'arrivée/de correspondance)
	 * 
	 * @param train
	 */
	public void majVoyageursDansTrainAvecResa(Train train, Itineraire itineraire, Set<Trajet> trajetsItineraire)
			throws TrainSansResaNotExpectedException {
		if (train instanceof TrainAvecResa) {
			TrainAvecResa trainAvecResa = (TrainAvecResa) train;
			List<Trajet> trajetsVoyageur;
			Iterator<Trajet> it;
			Trajet t, nextTrajet = null;

			em.getTransaction().begin();

			for (Trajet trajet : trajetsItineraire) {
				if (itineraire.getArretActuel().getGare().equals(trajet.getGareDepart())) {
					nextTrajet = trajet;
					break;
				}
			}
			List<Voyageur> voyageursToRemove = new ArrayList<Voyageur>();
			for (Voyageur voyageur : itineraire.getVoyageurs()) {
				// Les voyageurs qui doivent descendre
				trajetsVoyageur = voyageur.getVoyage().getTrajets();
				it = trajetsVoyageur.iterator();

				while (it.hasNext()) {
					t = it.next();
					// Voyageurs qui ont une correspondance
					if (itineraire.getArretActuel().getGare().equals(t.getGareDepart()) && !t.equals(nextTrajet)) {
						trainAvecResa.removeVoyageur(voyageur);
						voyageursToRemove.add(voyageur);
					}
					// Les voyageurs qui doivent monter
					if (itineraire.getArretActuel().getGare().equals(t.getGareDepart()) && t.equals(nextTrajet)
							&& LocalDateTime.now().isBefore(itineraire.getArretActuel().getHeureDepartDeGare())) {
						trainAvecResa.addVoyageur(voyageur);
					}
				}
				// Les voyageurs qui doivent descendre
				if (voyageur.getVoyage().getGareArrivee().equals(itineraire.getArretActuel().getGare())) {
					trainAvecResa.removeVoyageur(voyageur);
					voyageursToRemove.add(voyageur);
				}
			}
			itineraire.getVoyageurs().removeAll(voyageursToRemove);

			em.getTransaction().commit();
		} else {
			throw new TrainSansResaNotExpectedException("Expected only an instance of 'TrainAvecResa'");
		}
	}

	/**
	 * Ajouter les voyageurs contenus dans la liste passée en 2ème paramètre à
	 * l'itinéraire en 1er paramètre
	 * 
	 * @param itineraire
	 * @param voyageursToAdd
	 */
	public void mettreVoyageursDansItineraire(Itineraire itineraire, List<Voyageur> voyageursToAdd) {
		em.getTransaction().begin();
		itineraire.setVoyageurs(voyageursToAdd);
		em.getTransaction().commit();
	}

	public class TrainSansResaNotExpectedException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3544186233862870340L;

		public TrainSansResaNotExpectedException(String message) {
			super(message);
		}

	}

	/**
	 * Récupérer l'ensemble des voyages existants en BD
	 * 
	 * @return
	 */
	public List<Voyageur> getAllVoyageurs() {
		return (List<Voyageur>) em.createNamedQuery("Voyageur.getAllVoyageurs", Voyageur.class).getResultList();
	}

}
