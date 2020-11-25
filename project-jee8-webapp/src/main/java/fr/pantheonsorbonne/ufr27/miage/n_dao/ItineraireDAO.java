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

}
