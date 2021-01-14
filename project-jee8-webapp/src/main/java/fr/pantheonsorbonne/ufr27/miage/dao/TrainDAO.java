package fr.pantheonsorbonne.ufr27.miage.dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.jpa.Train;

@ManagedBean
@RequestScoped
public class TrainDAO {

	@Inject
	EntityManager em;

	/**
	 * Récupérer en BD le train d'id idTrain
	 * @param idTrain
	 * @return
	 */
	public Train getTrainById(int idTrain) {
		return em.createNamedQuery("Train.getTrainById", Train.class).setParameter("id", idTrain).getSingleResult();
	}

	/**
	 * Récupérer en BD le train ayant comme businessId celui passé en paramètre
	 * @param businessIdTrain
	 * @return
	 */
	public Train getTrainByBusinessId(String businessIdTrain) {
		return em.createNamedQuery("Train.getTrainByBusinessId", Train.class).setParameter("id", businessIdTrain)
				.getSingleResult();
	}

	/**
	 * Récupérer l'ensemble des trains présents en BD
	 * @return
	 */
	public List<Train> getAllTrains() {
		return (List<Train>) em.createNamedQuery("Train.getAllTrains", Train.class).getResultList();
	}

}
