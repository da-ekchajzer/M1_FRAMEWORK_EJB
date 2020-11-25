package fr.pantheonsorbonne.ufr27.miage.n_dao;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;

@ManagedBean
public class TrainDAO {

	@Inject
	EntityManager em;

	public Train getTrainById(int idTrain) {
		return em.createNamedQuery("Train.getTrainById", Train.class).setParameter("id", idTrain).getSingleResult();
	}

}
