package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyage;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyageur;

@ManagedBean
public class VoyageurDAO {

	@Inject
	EntityManager em;

	@Inject
	TrainDAO trainDAO;

	@Inject
	ItineraireDAO itineraireDAO;

	public List<Voyageur> getVoyageursByVoyage(Voyage v) {
		return (List<Voyageur>) em.createNamedQuery("Voyageur.getVoyageursByVoyage", Voyageur.class)
				.setParameter("id", v.getId()).getResultList();
	}

	public void majVoyageursDansTrainAvecResa(int idTrain, Arret arret) {
		TrainAvecResa train = (TrainAvecResa) trainDAO.getTrainById(idTrain);

		Itineraire itineraire = itineraireDAO.getItineraireByTrainEtEtat(idTrain, CodeEtatItinieraire.EN_COURS);

		for (Voyageur voyageur : itineraire.getVoyageurs()) {
			if (voyageur.getVoyage().getGareDeDepart().equals(arret.getGare())
					&& LocalDateTime.now().isBefore(arret.getHeureDepartDeGare())) {
				train.addVoyageurInTrain(voyageur);
			}

			if (voyageur.getVoyage().getGareArrivee().equals(arret.getGare())) {
				train.getVoyageurs().remove(voyageur);
			}
		}

		em.getTransaction().begin();
		// TODO
		em.getTransaction().commit();
	}
}
