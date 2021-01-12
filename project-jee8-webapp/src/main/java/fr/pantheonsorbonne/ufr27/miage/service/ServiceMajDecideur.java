package fr.pantheonsorbonne.ufr27.miage.service;

import java.util.Collection;
import java.util.Queue;

import fr.pantheonsorbonne.ufr27.miage.service.utils.Retard;

public interface ServiceMajDecideur {

	public void decideRetard(Retard retard, boolean isRetard);

	public Collection<Retard> getRetardsItineraireEnCorespondance(Retard retard);

	public void factoriseRetard(Queue<Retard> retards);
}