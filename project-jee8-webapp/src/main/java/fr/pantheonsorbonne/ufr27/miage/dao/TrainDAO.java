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

	public Train getTrainById(int idTrain) {
		return em.createNamedQuery("Train.getTrainById", Train.class).setParameter("id", idTrain).getSingleResult();
	}

	public Train getTrainByBusinessId(String businessIdTrain) {
		return em.createNamedQuery("Train.getTrainByBusinessId", Train.class).setParameter("id", businessIdTrain)
				.getSingleResult();
	}

	public List<Train> getAllItineraires() {
		return (List<Train>) em.createNamedQuery("Train.getAllTrains", Train.class).getResultList();
	}

}
