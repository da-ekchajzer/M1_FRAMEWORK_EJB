package fr.pantheonsorbonne.ufr27.miage.repository;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.dao.TrainDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.Train;

@ManagedBean
@RequestScoped
public class TrainRepository {

	@Inject
	TrainDAO trainDAO;

	/**
	 * Récupérer le train d'id idTrain
	 * @param idTrain
	 * @return
	 */
	public Train getTrainById(int idTrain) {
		return trainDAO.getTrainById(idTrain);
	}

	/**
	 * Récupérer le train ayant comme businessId (au format ENTIER) 
	 * celui passé en paramètre
	 * @param businessIdTrain
	 * @return
	 */
	public Train getTrainByBusinessId(int businessIdTrain) {
		String businessId = "T" + businessIdTrain;
		return trainDAO.getTrainByBusinessId(businessId);
	}

	/**
	 * Récupérer tous les trains existants
	 * @return
	 */
	public List<Train> getAllTrains() {
		return trainDAO.getAllTrains();
	}

}
