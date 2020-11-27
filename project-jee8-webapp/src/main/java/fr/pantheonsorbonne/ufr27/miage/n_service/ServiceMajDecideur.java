package fr.pantheonsorbonne.ufr27.miage.n_service;

import java.time.LocalTime;

public interface ServiceMajDecideur {
	public void decideMajTrainCreation(int idTrain, LocalTime estimationTempsRetard);
	public void decideMajTrainEnCours(int idTrain, LocalTime estimationTempsRetardEnCours);
	public void decideMajTrainFin(int idTrain);
}