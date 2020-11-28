package fr.pantheonsorbonne.ufr27.miage.n_repository;

import java.util.List;

import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.n_dao.GareDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;

public class GareRepository {

	@Inject
	GareDAO gareDAO;
	
	public List<Gare> getGaresByNom(String nom) {
		return gareDAO.getGaresByNom(nom);
	}
	
	
}
