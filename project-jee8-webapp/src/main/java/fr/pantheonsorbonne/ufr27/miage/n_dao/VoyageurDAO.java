package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyage;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyageur;

@ManagedBean
@RequestScoped
public class VoyageurDAO {

	@Inject
	EntityManager em;

	public List<Voyageur> getVoyageursByVoyageActuel(Voyage v) {
		return (List<Voyageur>) em.createNamedQuery("Voyageur.getVoyageursByVoyageActuel", Voyageur.class)
				.setParameter("id", v.getId()).getResultList();
	}

	public void majVoyageursDansTrainAvecResa(TrainAvecResa train, Itineraire itineraire,
			Set<Trajet> trajetsItineraire) {
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
			trajetsVoyageur = voyageur.getVoyageActuel().getTrajets();
			it = trajetsVoyageur.iterator();

			while (it.hasNext()) {
				t = it.next();
				// Voyageurs qui ont une correspondance
				if (itineraire.getArretActuel().getGare().equals(t.getGareDepart()) && !t.equals(nextTrajet)) {
					train.removeVoyageur(voyageur);
					voyageursToRemove.add(voyageur);
				}
				// Les voyageurs qui doivent monter
				if (itineraire.getArretActuel().getGare().equals(t.getGareDepart()) && t.equals(nextTrajet)
						&& LocalDateTime.now().isBefore(itineraire.getArretActuel().getHeureDepartDeGare())) {
					train.addVoyageur(voyageur);
				}
			}
			// Les voyageurs qui doivent descendre
			if (voyageur.getVoyageActuel().getGareArrivee().equals(itineraire.getArretActuel().getGare())) {
				train.removeVoyageur(voyageur);
				voyageursToRemove.add(voyageur);
			}
		}
		itineraire.getVoyageurs().removeAll(voyageursToRemove);
		
		em.getTransaction().commit();
	}

	public void mettreVoyageursDansItineraire(Itineraire itineraire, List<Voyageur> voyageursToAdd) {
		em.getTransaction().begin();
		// On ajoute les voyageurs dans l'itinéraire
		itineraire.setVoyageurs(voyageursToAdd);
		em.getTransaction().commit();
	}

	public void majVoyageActuelDesVoyageurs(Voyage newVoyageActuel, List<Voyageur> voyageursToUpdate) {
		em.getTransaction().begin();
		// On met à jour le voyage actuel des voyageurs
		for (Voyageur v : voyageursToUpdate) {
			v.setVoyageActuel(newVoyageActuel);
		}
		em.getTransaction().commit();
	}

}
