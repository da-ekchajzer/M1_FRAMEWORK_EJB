package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Trajet;

@ManagedBean
@RequestScoped
public class ItineraireDAO {

	@Inject
	EntityManager em;

	public Itineraire getItineraireById(int idItineraire) {
		return em.createNamedQuery("Itineraire.getItineraireById", Itineraire.class).setParameter("id", idItineraire)
				.getSingleResult();
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
	
	public List<Itineraire> getAllItineraires() {
		return (List<Itineraire>) em.createNamedQuery("Itineraire.getAllItineraires", Itineraire.class).getResultList();
	}

	public void majEtatItineraire(Itineraire itineraire, CodeEtatItinieraire newEtat) {
		em.getTransaction().begin();
		itineraire.setEtat(newEtat.getCode());
		em.getTransaction().commit();
	}

	public void majArretActuel(Itineraire itineraire, Arret arret) {
		em.getTransaction().begin();
		itineraire.setArretActuel(arret);
		em.getTransaction().commit();
	}

	

	public void ajouterUnArretDansUnItineraire(Itineraire itineraire, Arret arret, Gare gare, List<Trajet> trajets) {
		em.getTransaction().begin();
		// On ajoute l'arrêt à l'itinéraire
		for (int i = 0; i < trajets.size(); i++) {
			if (gare.equals(trajets.get(i).getGareArrivee())) {
				if (i == trajets.size() - 1) {
					// arrêt qu'on s'ajoute à la fin
					itineraire.addArret(arret);
				} else {
					// arrêt qu'on ajoute en cours d'itinéraire
					List<Arret> arretsDeTransition = new LinkedList<Arret>();
					int length = itineraire.getArretsDesservis().size();
					for (int j = i + 1; j < length; j++) {
						arretsDeTransition.add(itineraire.getArretsDesservis().remove(j));
					}
					itineraire.addArret(arret);
					itineraire.getArretsDesservis().addAll(arretsDeTransition);
				}
			}
		}
		em.getTransaction().commit();
	}

	public void retarderTrain(LocalTime tempsRetard, Arret arretRetarde, Itineraire itineraire) {
		em.getTransaction().begin();

		if (LocalDateTime.now().isBefore(arretRetarde.getHeureArriveeEnGare())) {
			arretRetarde.setHeureArriveeEnGare(
					arretRetarde.getHeureArriveeEnGare().plus(tempsRetard.toSecondOfDay(), ChronoUnit.SECONDS));
		}
		
		if (LocalDateTime.now().isBefore(arretRetarde.getHeureDepartDeGare())) {
			arretRetarde.setHeureDepartDeGare(
					arretRetarde.getHeureDepartDeGare().plus(tempsRetard.toSecondOfDay(), ChronoUnit.SECONDS));
		}

		for (Arret a : itineraire.getArretsDesservis()) {
			if (a.getHeureArriveeEnGare().isAfter(arretRetarde.getHeureArriveeEnGare())) {
				a.setHeureArriveeEnGare(
						a.getHeureArriveeEnGare().plus(tempsRetard.toSecondOfDay(), ChronoUnit.SECONDS));
				if (a.getHeureDepartDeGare() != null) {
					a.setHeureDepartDeGare(
							a.getHeureDepartDeGare().plus(tempsRetard.toSecondOfDay(), ChronoUnit.SECONDS));
				}
			}
		}

		em.getTransaction().commit();
	}

	public List<Itineraire> getAllItinerairesByEtat(CodeEtatItinieraire codeEtatItinieraire) {
		return (List<Itineraire>) em.createNamedQuery("Itineraire.getAllItinerairesByEtat", Itineraire.class)
				.setParameter("etat", codeEtatItinieraire.getCode()).getResultList();
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
