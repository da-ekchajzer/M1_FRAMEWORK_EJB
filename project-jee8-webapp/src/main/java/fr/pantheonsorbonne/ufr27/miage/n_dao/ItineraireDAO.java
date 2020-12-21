package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;

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
		return em.createNamedQuery("Itineraire.getItineraireByBusinessId", Itineraire.class).setParameter("id", businessIdItineraire)
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
	
	public void ajouterUnArretEnCoursItineraire(Itineraire itineraire, Arret arret) {
		List<Arret> arretsDeTransition = new LinkedList<Arret>();
		
		// Ajouter l'arrêt à la fin de l'itinéraire
		if(arret.getHeureDepartDeGare() == null || arret.getHeureArriveeEnGare() == null) {
			System.err.println("Veuillez utiliser la méthode 'ajouterUnArretEnBoutItineraire()'");
			return;
		}
		// Ajouter l'arrêt en cours d'itinéraire
		else {
			// Déterminer la position à laquelle on doit ajouter l'arrêt
			for (int i = 0 ; i < itineraire.getArretsDesservis().size() ; i++) {
				if(itineraire.getArretsDesservis().get(i).getHeureDepartDeGare() != null && 
						itineraire.getArretsDesservis().get(i).getHeureDepartDeGare().isBefore(arret.getHeureDepartDeGare())) {
					arretsDeTransition.add(itineraire.getArretsDesservis().get(i));
				} else {
					arretsDeTransition.add(arret);
					for(int j = i ; j < itineraire.getArretsDesservis().size() ; j++) {
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
	
	/**
	 * 
	 * @param itineraire
	 * @param arret
	 * @param heure heureDeDepart de l'ancienne gare d'arrivée ou heureArrivee de l'ancienne gare de départ
	 */
	public void ajouterUnArretEnBoutItineraire(Itineraire itineraire, Arret arret, LocalDateTime heure) {
		List<Arret> arretsDeTransition = new LinkedList<Arret>();

		if(arret.getHeureArriveeEnGare() == null) {
			Arret ancienDepartus = itineraire.getArretsDesservis().get(0);
			ancienDepartus.setHeureArriveeEnGare(heure);
			arretsDeTransition.add(arret);
			arretsDeTransition.addAll(itineraire.getArretsDesservis());
		} else if(arret.getHeureDepartDeGare() == null) {
			Arret ancienTerminus = itineraire.getArretsDesservis().get(itineraire.getArretsDesservis().size()-1);
			ancienTerminus.setHeureDepartDeGare(heure);
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

	public void retarderTrain(LocalTime tempsRetard, Arret arretRetarde, Itineraire itineraire) {
		em.getTransaction().begin();

		// S'il n'y a pas d'arrêt actuel (=> Itineraire en attente) alors on retarde tous les arrêts
		if(arretRetarde == null) {
			for (Arret a : itineraire.getArretsDesservis()) {
				a.setHeureArriveeEnGare(
						a.getHeureArriveeEnGare().plus(tempsRetard.toSecondOfDay(), ChronoUnit.SECONDS));
				if (a.getHeureDepartDeGare() != null) {
					a.setHeureDepartDeGare(
							a.getHeureDepartDeGare().plus(tempsRetard.toSecondOfDay(), ChronoUnit.SECONDS));
				}
			}
		} 
		// Sinon l'itinéraire est en cours, on retarde que les arrêts après l'arret actuel (arretRetarde)
		else {
			if (arretRetarde.getHeureArriveeEnGare() != null && 
					LocalDateTime.now().isBefore(arretRetarde.getHeureArriveeEnGare())) {
				arretRetarde.setHeureArriveeEnGare(
						arretRetarde.getHeureArriveeEnGare().plus(tempsRetard.toSecondOfDay(), ChronoUnit.SECONDS));
			}
			
			if (arretRetarde.getHeureDepartDeGare() != null && 
					LocalDateTime.now().isBefore(arretRetarde.getHeureDepartDeGare())) {
				arretRetarde.setHeureDepartDeGare(
						arretRetarde.getHeureDepartDeGare().plus(tempsRetard.toSecondOfDay(), ChronoUnit.SECONDS));
			}
			
			// On va y stocker les différents arrêts à retarder en fonction des cas suivants
			Set<Arret> arretsARetardes = new TreeSet<>();
			// Si l'arret actuel est la gare de départ (arretRetarde.getHeureArriveeEnGare == null), 
			// on retarde tous les arrêts de l'itinéraire sauf le premier
			if(arretRetarde.getHeureArriveeEnGare() == null) {
				arretsARetardes.addAll(itineraire.getArretsDesservis());
			}
			// Si l'arret actuel est le dernier de l'itinéraire, on n'a aucun arret a retarder
			// Si l'arret actuel est un arret en plein milieu de l'itinéraire, on ne retarde que les suivants			
			else if(arretRetarde.getHeureArriveeEnGare() != null && arretRetarde.getHeureDepartDeGare() != null) {
				for(Arret a : itineraire.getArretsDesservis()) {
					if (a.getHeureArriveeEnGare().isAfter(arretRetarde.getHeureArriveeEnGare())) {
						arretsARetardes.add(a);
					}
				}
			}
			// On retarde chaque arret compris dans arretsARetardes
			for (Arret a : arretsARetardes) {
				// Si arretActuel = gare de départ de l'itinéraire on ne touche pas son heure d'arrivée en gare
				if(a.getHeureArriveeEnGare() != null) {
					a.setHeureArriveeEnGare(
							a.getHeureArriveeEnGare().plus(tempsRetard.toSecondOfDay(), ChronoUnit.SECONDS));
				}
				// Si arretActuel = gare d'arrivée de l'itinéraire on ne touche pas son heure de départ de gare
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
