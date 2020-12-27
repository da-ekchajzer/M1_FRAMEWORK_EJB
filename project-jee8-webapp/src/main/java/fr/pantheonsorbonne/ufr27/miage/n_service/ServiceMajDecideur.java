package fr.pantheonsorbonne.ufr27.miage.n_service;

import fr.pantheonsorbonne.ufr27.miage.n_service.utils.Retard;

public interface ServiceMajDecideur {
	public void decideRetard(Retard retard);
	public void decideMajTrainFin(int idTrain);
}