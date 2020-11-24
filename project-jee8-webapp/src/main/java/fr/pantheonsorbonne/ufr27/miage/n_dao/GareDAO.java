package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;

@ManagedBean
public class GareDAO {

	@Inject
	EntityManager em;

	@SuppressWarnings("unchecked")
	public List<Gare> getGaresByNom(String nom) {
		return (List<Gare>) em.createNativeQuery("SELECT g " + "FROM GARE g " + "WHERE g.NOM = ?").setParameter(1, nom)
				.getResultList();
	}
}
