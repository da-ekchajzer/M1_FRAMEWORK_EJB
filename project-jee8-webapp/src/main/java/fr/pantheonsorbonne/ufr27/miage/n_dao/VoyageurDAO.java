package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyage;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyageur;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.TrajetRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.VoyageRepository;

@ManagedBean
@RequestScoped
public class VoyageurDAO {

	@Inject
	EntityManager em;

	@Inject
	ItineraireRepository itineraireRepository;

	@Inject
	TrajetRepository trajetRepository;

	@Inject
	VoyageRepository voyageRepository;

	public List<Voyageur> getVoyageursByVoyage(Voyage v) {
		return (List<Voyageur>) em.createNamedQuery("Voyageur.getVoyageursByVoyage", Voyageur.class)
				.setParameter("id", v.getId()).getResultList();
	}

	public void majVoyageursDansTrainAvecResa(TrainAvecResa train, Itineraire itineraire, Set<Trajet> trajetsItineraire) {
		Set<Trajet> trajetsVoyageur;
		Iterator<Trajet> it;
		Trajet t, nextTrajet = null;
		
		em.getTransaction().begin();
		// TODO : est-ce que les voyageurs dans ajoutés dans la liste de l'itinéraire au
		// moment de leur réservation ?

		for (Trajet trajet : trajetsItineraire) {
			if (itineraire.getArretActuel().getGare().equals(trajet.getGareDepart())) {
				nextTrajet = trajet;
				break;
			}
		}

		for (Voyageur voyageur : itineraire.getVoyageurs()) {
			// Les voyageurs qui doivent descendre
			trajetsVoyageur = new TreeSet<Trajet>(voyageur.getVoyage().getTrajets());
			it = trajetsVoyageur.iterator();

			while (it.hasNext()) {
				t = it.next();
				if (itineraire.getArretActuel().getGare().equals(t.getGareDepart()) && !t.equals(nextTrajet)) {
					train.getVoyageurs().remove(voyageur);
					itineraire.getVoyageurs().remove(voyageur);
				}
				// Les voyageurs qui doivent monter
				if (itineraire.getArretActuel().getGare().equals(t.getGareDepart()) && t.equals(nextTrajet)
						&& LocalDateTime.now().isBefore(itineraire.getArretActuel().getHeureDepartDeGare())) {
					train.getVoyageurs().add(voyageur);
				}
			}
			// Les voyageurs qui doivent descendre
			if (voyageur.getVoyage().getGareArrivee().equals(itineraire.getArretActuel().getGare())) {
				train.getVoyageurs().remove(voyageur);
				itineraire.getVoyageurs().remove(voyageur);
			}
		}
		em.getTransaction().commit();
	}

	
	
	public void mettreVoyageursDansItineraire(Itineraire itineraire, List<Voyageur> voyageursToAdd) {
		em.getTransaction().begin();
		// On ajoute les voyageurs dans l'itinéraire
		itineraire.setVoyageurs(voyageursToAdd);
		em.getTransaction().commit();
	}

}
