package fr.pantheonsorbonne.ufr27.miage.n_repository;

import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.n_dao.TrainDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;

public class TrainRepository {
	
	@Inject
	TrainDAO trainDAO;
	
	public Train getTrainById(int idTrain) {
		return trainDAO.getTrainById(idTrain);
	}
}
