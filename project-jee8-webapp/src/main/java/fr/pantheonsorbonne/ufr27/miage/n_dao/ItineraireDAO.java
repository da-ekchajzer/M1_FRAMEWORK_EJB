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

	public void ajouterIncidentItineraire(int idItineraire, int idIncident) {
		Itineraire itineraire = em.createNamedQuery("Itineraire.getItineraireById", Itineraire.class)
				.setParameter("id", idItineraire).getSingleResult();

		Incident incident = em.createNamedQuery("IncidentDAO.getIncidentById", Incident.class)
				.setParameter("id", idIncident).getSingleResult();

		em.getTransaction().begin();
		itineraire.setIncident(incident);
		em.getTransaction().commit();
	}

	public Itineraire recupItineraireEnCoursOuLeProchain(int idTrain) {
		Itineraire itineraire = em.createNamedQuery("Itineraire.getItineraireByTrainEtEtat", Itineraire.class)
				.setParameter("idTrain", idTrain).setParameter("etat", CodeEtatItinieraire.EN_COURS).getSingleResult();

		if (itineraire == null) {
			itineraire = em.createNamedQuery("Itineraire.getItineraireByTrainEtEtat", Itineraire.class)
					.setParameter("idTrain", idTrain).setParameter("etat", CodeEtatItinieraire.EN_INCIDENT)
					.getSingleResult();
		}

		if (itineraire == null) {
			List<Itineraire> itineraires = em
					.createNamedQuery("Itineraire.getItineraireByTrainEtEtat", Itineraire.class)
					.setParameter("idTrain", idTrain).setParameter("etat", CodeEtatItinieraire.EN_ATTENTE)
					.getResultList();

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
		Itineraire itineraire = em.createNamedQuery("Itineraire.getItineraireById", Itineraire.class)
				.setParameter("id", idItineraire).getSingleResult();
		
		em.getTransaction().begin();
		itineraire.setEtat(newEtat);
		em.getTransaction().commit();
	}

	public void updateArretActuel(int idTrain, Arret arret) {
		Itineraire itineraire = em.createNamedQuery("Itineraire.getItineraireByTrainEtEtat", Itineraire.class)
				.setParameter("idTrain", idTrain).setParameter("etat", CodeEtatItinieraire.EN_COURS).getSingleResult();
		
		em.getTransaction().begin();
		itineraire.setArretActuel(arret);
		em.getTransaction().commit();
	}

}
