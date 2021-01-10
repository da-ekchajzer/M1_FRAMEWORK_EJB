package fr.pantheonsorbonne.ufr27.miage.n_repository;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.n_dao.TrainDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;

@ManagedBean
@RequestScoped
public class TrainRepository {

	@Inject
	TrainDAO trainDAO;

	public Train getTrainById(int idTrain) {
		return trainDAO.getTrainById(idTrain);
	}

	public Train getTrainByBusinessId(int businessIdTrain) {
		String businessId = "T" + businessIdTrain;
		return trainDAO.getTrainByBusinessId(businessId);
	}

	public Train getTrainByBusinessId(String businessIdTrain) {
		return trainDAO.getTrainByBusinessId(businessIdTrain);
	}

	public List<Train> getAllTrains() {
		return trainDAO.getAllItineraires();
	}

}
