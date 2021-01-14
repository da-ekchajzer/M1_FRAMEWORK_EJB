package fr.pantheonsorbonne.ufr27.miage.repository;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.dao.GareDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;

@ManagedBean
@RequestScoped
public class GareRepository {

	@Inject
	GareDAO gareDAO;
	
	/**
	 * Récupérer une gare par son nom
	 * @param nom
	 * @return
	 */
	public List<Gare> getGaresByNom(String nom) {
		return gareDAO.getGaresByNom(nom);
	}
	
	
}
