package fr.pantheonsorbonne.ufr27.miage.n_repository;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fr.pantheonsorbonne.ufr27.miage.n_dao.GareDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;

@ManagedBean
@RequestScoped
public class GareRepository {

	@Inject
	GareDAO gareDAO;
	
	public List<Gare> getGaresByNom(String nom) {
		return gareDAO.getGaresByNom(nom);
	}
	
	
}
