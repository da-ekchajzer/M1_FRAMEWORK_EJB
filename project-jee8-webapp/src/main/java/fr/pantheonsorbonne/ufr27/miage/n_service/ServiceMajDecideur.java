package fr.pantheonsorbonne.ufr27.miage.n_service;

import java.sql.Time;

public interface ServiceMajDecideur {
	public void decideMajTrainCreation(int idTrain, Time estimationTempsRetard);
	public void decideMajTrainEnCours(int idTrain, Time estimationTempsRetardEnCours);
	public void decideMajTrainFin(int idTrain);
}
