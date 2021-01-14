package fr.pantheonsorbonne.ufr27.miage.service;

import java.time.temporal.ChronoUnit;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.IncidentJAXB;

public interface ServiceIncident {

	/**
	 * Créer un incident JPA et le persister en BD à partir
	 * de l'incident JAXB reçu par l'API REST suite à un envoi par le train d'id idTrain 
	 * @param idTrain
	 * @param inc
	 * @return
	 */
	public boolean creerIncident(int idTrain, IncidentJAXB inc);

	/**
	 * Si l'incident est toujours en cours et que son heure théorique de fin est
	 * dépassée au moment de l'appel de cette méthode alors on ajoute ajoutDuree à
	 * l'heure théorique de fin de l'incident et ainsi ce dernier est prolongé.
	 * Sinon, l'incident est clôturé est le traffic reprend son cours.
	 * 
	 * @param idTrain
	 * @param etatIncident
	 * @param ajoutDuree      : l'incident sera prolongé de cette durée si son heure
	 *                        théorique de fin est dépassée et qu'il est en cours
	 * @param chronoUnitDuree : l'unité de temps du paramètre ajoutDuree
	 * @return
	 */
	public boolean majEtatIncident(int idTrain, int etatIncident, long ajoutDuree, ChronoUnit chronoUnitDuree);
}
