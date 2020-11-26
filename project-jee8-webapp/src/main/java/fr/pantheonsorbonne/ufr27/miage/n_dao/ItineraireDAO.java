package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;

@ManagedBean
public class ItineraireDAO {

	@Inject
	EntityManager em;

	@Inject
	ArretDAO arretDao;

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

	public void updateArretActuel(int idTrain, Arret arret) {
		Itineraire itineraire = getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);

		em.getTransaction().begin();
		itineraire.setArretActuel(arret);
		em.getTransaction().commit();
	}
	
	/**
	 * @author Mathieu
	 * 26/11/2020 (Matin)
	 * 
	 * La méthode était dans ArretDAO je l'ai juste déplacé
	 * 
	 * TODO : Si on suppr l'arrêt juste comme ça, 
	 * ça le suppr pour tous les itinéraires qui l'ont dans leur liste, c'est OK ?
	 * 
	 * @param idTrain
	 * @param arret
	 * @return
	 */
	public boolean supprimerArret(int idTrain, Arret arret) {
		// On récupère l'itinéraire associé au train
		Itineraire itineraire = getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);

		int nbArretsAvantSuppression = itineraire.getGaresDesservies().size();

		// On supprime l'arrêt de l'itinéraire
		em.getTransaction().begin();
		em.remove(arret);
		em.getTransaction().commit();

		return itineraire.getGaresDesservies().size() == nbArretsAvantSuppression - 1;
	}
	
	/**
	 * @author Mathieu
	 * 26/11/2020 (Matin)
	 * 
	 * @param idTrain
	 * @param idArret
	 * @return
	 */
	public boolean ajouterUnArretDansUnItineraire(int idTrain, int idArret) {
		// On récupère l'itinéraire associé au train
		Itineraire itineraire = getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);

		int nbArretsAvantSuppression = itineraire.getGaresDesservies().size();

		// On ajoute l'arrêt à l'itinéraire
		// TODO : Comment on a la position de l'arrêt à ajouter dans la liste ?

		return itineraire.getGaresDesservies().size() == nbArretsAvantSuppression + 1;
	}
	
	public void delayTrain(int idTrain, int horaire) {
		// TODO
	}

}
