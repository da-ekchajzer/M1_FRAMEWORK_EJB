package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Trajet;

@ManagedBean
public class ItineraireDAO {

	@Inject
	EntityManager em;

	@Inject
	ArretDAO arretDao;

	@Inject
	TrajetDAO trajetDAO;

	public Itineraire getItineraireById(int idItineraire) {
		return em.createNamedQuery("Itineraire.getItineraireById", Itineraire.class).setParameter("id", idItineraire)
				.getSingleResult();
	}

	public Itineraire getItineraireByTrainEtEtat(int idTrain, CodeEtatItinieraire etat) {
		return em.createNamedQuery("Itineraire.getItineraireByTrainEtEtat", Itineraire.class)
				.setParameter("idTrain", idTrain).setParameter("etat", etat).getSingleResult();
	}

	public List<Itineraire> getAllItinerairesByTrainEtEtat(int idTrain, CodeEtatItinieraire etat) {
		return (List<Itineraire>) em.createNamedQuery("Itineraire.getItineraireByTrainEtEtat", Itineraire.class)
				.setParameter("idTrain", idTrain).setParameter("etat", etat).getResultList();
	}

	public void ajouterIncidentItineraire(int idItineraire, int idIncident) {
		Itineraire itineraire = getItineraireById(idItineraire);

		Incident incident = em.createNamedQuery("IncidentDAO.getIncidentById", Incident.class)
				.setParameter("id", idIncident).getSingleResult();

		em.getTransaction().begin();
		itineraire.setIncident(incident);
		em.getTransaction().commit();
	}

	public Itineraire recupItineraireEnCoursOuLeProchain(int idTrain) {
		Itineraire itineraire = getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);

		if (itineraire == null) {
			itineraire = getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_INCIDENT);
		}

		if (itineraire == null) {
			List<Itineraire> itineraires = getAllItinerairesByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_ATTENTE);

			List<Arret> arrets = new ArrayList<Arret>();

			for (Itineraire i : itineraires) {
				arrets.add(i.getGaresDesservies().get(0));
			}

			itineraire = itineraires.get(0);

			for (int n = 0; n < itineraires.size(); n++) {
				if (arrets.get(n).getHeureDepartDeGare()
						.isBefore(itineraire.getGaresDesservies().get(0).getHeureDepartDeGare()))
					itineraire = itineraires.get(n);
			}
		}

		return itineraire;

	}

	public void majEtatItineraire(int idItineraire, int newEtat) {
		Itineraire itineraire = getItineraireById(idItineraire);

		em.getTransaction().begin();
		itineraire.setEtat(newEtat);
		em.getTransaction().commit();
	}

	public void majArretActuel(int idTrain, Arret arret) {
		Itineraire itineraire = getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);

		em.getTransaction().begin();
		itineraire.setArretActuel(arret);
		em.getTransaction().commit();
	}

	public boolean supprimerArretDansUnItineraire(int idTrain, Arret arret) {
		// On récupère l'itinéraire associé au train
		Itineraire itineraire = getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);

		int nbArretsAvantSuppression = itineraire.getGaresDesservies().size();

		// On supprime l'arrêt de l'itinéraire
		em.getTransaction().begin();
		itineraire.getGaresDesservies().remove(arret);
		em.remove(arret);
		em.getTransaction().commit();

		return itineraire.getGaresDesservies().size() == nbArretsAvantSuppression - 1;
	}

	/*
	 * TODO : ajouter ce cas de figure dans le BDDFillerServiceImpl
	 */
	public boolean ajouterUnArretDansUnItineraire(int idTrain, Arret arret) {
		// On récupère l'itinéraire associé au train
		Itineraire itineraire = getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);
		
		Gare gare = arret.getGare();

		List<Trajet> trajets = trajetDAO.getTrajetsByItineraire(itineraire);

		int nbArretsAvantAjout = itineraire.getGaresDesservies().size();

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
					int length = itineraire.getGaresDesservies().size();
					for (int j = i + 1; j < length; j++) {
						arretsDeTransition.add(itineraire.getGaresDesservies().remove(j));
					}
					itineraire.addArret(arret);
					itineraire.getGaresDesservies().addAll(arretsDeTransition);
				}
			}
		}
		
		em.getTransaction().commit();

		return itineraire.getGaresDesservies().size() == nbArretsAvantAjout + 1;
	}

	public void retarderTrain(int idTrain, int tempsRetard, ChronoUnit chronoUnitType) {
		// On récupère l'itinéraire associé au train
		Itineraire itineraire = getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);

		Arret arretActuel = itineraire.getArretActuel();
		
		em.getTransaction().begin();

		if (LocalDateTime.now().isBefore(arretActuel.getHeureDepartDeGare())) {
			arretActuel.setHeureDepartDeGare(arretActuel.getHeureDepartDeGare().plus(tempsRetard, chronoUnitType));
		}

		for (Arret a : itineraire.getGaresDesservies()) {
			if (a.getHeureArriveeEnGare().isAfter(arretActuel.getHeureArriveeEnGare())) {
				a.setHeureArriveeEnGare(a.getHeureArriveeEnGare().plus(tempsRetard, chronoUnitType));
				a.setHeureDepartDeGare(a.getHeureDepartDeGare().plus(tempsRetard, chronoUnitType));
			}
		}
		
		em.getTransaction().commit();
	}

}
