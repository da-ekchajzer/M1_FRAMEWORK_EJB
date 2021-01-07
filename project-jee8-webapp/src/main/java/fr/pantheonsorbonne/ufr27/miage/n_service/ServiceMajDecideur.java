package fr.pantheonsorbonne.ufr27.miage.n_service;

import java.util.Collection;
import java.util.Queue;

import fr.pantheonsorbonne.ufr27.miage.n_service.utils.Retard;

public interface ServiceMajDecideur {
	public void decideRetard(Retard retard);
	public Collection<Retard> getRetardsItineraireEnCorespondance(Retard retard);
	public void factoriseRetard(Queue<Retard> retards);
}