package fr.pantheonsorbonne.ufr27.miage.dao;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire.CodeEtatItinieraire;

@ManagedBean
@RequestScoped
public class ItineraireDAO {

	@Inject
	EntityManager em;

	public Itineraire getItineraireById(int idItineraire) {
		return em.createNamedQuery("Itineraire.getItineraireById", Itineraire.class).setParameter("id", idItineraire)
				.getSingleResult();
	}

	public Itineraire getItineraireByBusinessId(String businessIdItineraire) {
		em.clear();
		return em.createNamedQuery("Itineraire.getItineraireByBusinessId", Itineraire.class)
				.setParameter("id", businessIdItineraire).getSingleResult();
	}

	public Itineraire getItineraireByTrainEtEtat(int idTrain, CodeEtatItinieraire etat)
			throws MulitpleResultsNotExpectedException {

		List<Itineraire> itineraires = getAllItinerairesByTrainEtEtat(idTrain, etat);
		if (itineraires.isEmpty()) {
			return null;
		} else if (itineraires.size() > 1) {
			throw new MulitpleResultsNotExpectedException("Expected only one 'Itineraire'");
		}
		return itineraires.get(0);
	}

	public List<Itineraire> getAllItinerairesByTrainEtEtat(int idTrain, CodeEtatItinieraire etat) {
		return (List<Itineraire>) em.createNamedQuery("Itineraire.getItineraireByTrainEtEtat", Itineraire.class)
				.setParameter("idTrain", idTrain).setParameter("etat", etat.getCode()).getResultList();
	}

	public List<Itineraire> getAllItinerairesByEtat(CodeEtatItinieraire codeEtatItinieraire) {
		return (List<Itineraire>) em.createNamedQuery("Itineraire.getAllItinerairesByEtat", Itineraire.class)
				.setParameter("etat", codeEtatItinieraire.getCode()).getResultList();
	}

	public List<Itineraire> getAllItineraires() {
		return (List<Itineraire>) em.createNamedQuery("Itineraire.getAllItineraires", Itineraire.class).getResultList();
	}

	public void majEtatItineraire(Itineraire itineraire, CodeEtatItinieraire newEtat) {
		em.getTransaction().begin();
		if (itineraire.getEtat() == CodeEtatItinieraire.EN_INCIDENT.getCode()
				&& newEtat.equals(CodeEtatItinieraire.EN_COURS)) {
			itineraire.setIncident(null);
		}
		itineraire.setEtat(newEtat.getCode());
		em.getTransaction().commit();
	}

	public void majArretActuel(Itineraire itineraire, Arret arret) {
		em.getTransaction().begin();
		itineraire.setArretActuel(arret);
		em.getTransaction().commit();
	}

	public void supprimerArretDansUnItineraire(Itineraire itineraire, Arret arret) {
		em.getTransaction().begin();
		itineraire.getArretsDesservis().remove(arret);
		em.getTransaction().commit();
	}

	public void ajouterUnArretDansItineraire(Itineraire itineraire, Arret arret) {
		// Ajouter l'arrêt à la fin de l'itinéraire
		if (arret.getHeureDepartDeGare() == null) {
			System.err.println("Le nouvel arret ne doit pas etre un arret de fin d'itineraire");
			return;
		}
		// Ajouter l'arrêt en cours d'itinéraire
		else {
			em.getTransaction().begin();
			em.persist(arret);
			// Déterminer la position à laquelle on doit ajouter l'arrêt
			itineraire.addArret(arret);
			int lastIndex = itineraire.getArretsDesservis().size() - 1;
			for (int i = 1; i < lastIndex; i++) {
				// L'ordre des id des arrêts de la table de jointure avec les itinéraires ne
				// peut être changé, ce sont donc les données des arrêts qui sont déplacées et
				// pas les objets directement
				if (itineraire.getArretsDesservis().get(i).isAfter(itineraire.getArretsDesservis().get(lastIndex))) {
					Gare gi = itineraire.getArretsDesservis().get(i).getGare();
					LocalDateTime hai = itineraire.getArretsDesservis().get(i).getHeureArriveeEnGare();
					LocalDateTime hdi = itineraire.getArretsDesservis().get(i).getHeureDepartDeGare();
					itineraire.getArretsDesservis().get(i)
							.setGare(itineraire.getArretsDesservis().get(lastIndex).getGare());
					itineraire.getArretsDesservis().get(i).setHeureArriveeEnGare(
							itineraire.getArretsDesservis().get(lastIndex).getHeureArriveeEnGare());
					itineraire.getArretsDesservis().get(i).setHeureDepartDeGare(
							itineraire.getArretsDesservis().get(lastIndex).getHeureDepartDeGare());
					itineraire.getArretsDesservis().get(lastIndex).setGare(gi);
					itineraire.getArretsDesservis().get(lastIndex).setHeureArriveeEnGare(hai);
					itineraire.getArretsDesservis().get(lastIndex).setHeureDepartDeGare(hdi);
				}
			}
			em.getTransaction().commit();
		}

	}

	public class MulitpleResultsNotExpectedException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6747179329715195790L;

		public MulitpleResultsNotExpectedException(String message) {
			super(message);
		}

	}

}
