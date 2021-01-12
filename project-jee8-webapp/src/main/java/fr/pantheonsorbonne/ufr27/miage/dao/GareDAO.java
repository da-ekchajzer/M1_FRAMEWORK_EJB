package fr.pantheonsorbonne.ufr27.miage.dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;

@ManagedBean
@RequestScoped
public class GareDAO {

	@Inject
	EntityManager em;

	@SuppressWarnings("unchecked")
	public List<Gare> getGaresByNom(String nom) {
		return (List<Gare>) em.createNamedQuery("Gare.getGaresByNom").setParameter("nom", nom).getResultList();
	}
	
}
