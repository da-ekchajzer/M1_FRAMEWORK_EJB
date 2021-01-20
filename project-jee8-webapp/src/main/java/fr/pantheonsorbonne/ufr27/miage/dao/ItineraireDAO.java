package fr.pantheonsorbonne.ufr27.miage.dao;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;
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

	public void ajouterUnArretEnCoursItineraire(Itineraire itineraire, Arret arret) {
		List<Arret> arretsDeTransition = new LinkedList<Arret>();

		// Ajouter l'arrêt à la fin de l'itinéraire
		if (arret.getHeureDepartDeGare() == null) {
			System.err.println("Veuillez utiliser la méthode 'ajouterUnArretEnFinItineraire()'");
			return;
		}
		// Ajouter l'arrêt en cours d'itinéraire
		else {
			// Déterminer la position à laquelle on doit ajouter l'arrêt
			for (int i = 0; i < itineraire.getArretsDesservis().size(); i++) {
				if (itineraire.getArretsDesservis().get(i).getHeureDepartDeGare() != null && itineraire
						.getArretsDesservis().get(i).getHeureDepartDeGare().isBefore(arret.getHeureDepartDeGare())) {
					arretsDeTransition.add(itineraire.getArretsDesservis().get(i));
				} else {
					arretsDeTransition.add(arret);
					for (int j = i; j < itineraire.getArretsDesservis().size(); j++) {
						arretsDeTransition.add(itineraire.getArretsDesservis().get(j));
					}
					break;
				}
			}
		}

		em.getTransaction().begin();
		itineraire.setArretsDesservis(arretsDeTransition);
		em.getTransaction().commit();
	}

	public void ajouterUnArretEnFinItineraire(Itineraire itineraire, Arret arret, LocalDateTime heureDepartToAdd) {
		List<Arret> arretsDeTransition = new LinkedList<Arret>();

		if (arret.getHeureDepartDeGare() == null) {
			Arret ancienTerminus = itineraire.getArretsDesservis().get(itineraire.getArretsDesservis().size() - 1);
			ancienTerminus.setHeureDepartDeGare(heureDepartToAdd);
			arretsDeTransition.addAll(itineraire.getArretsDesservis());
			arretsDeTransition.add(arret);
		} else {
			System.err.println("Veuillez utiliser la méthode 'ajouterUnArretEnCoursItineraire()'");
			return;
		}

		em.getTransaction().begin();
		itineraire.setArretsDesservis(arretsDeTransition);
		em.getTransaction().commit();
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
