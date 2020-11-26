package fr.pantheonsorbonne.ufr27.miage.n_dao;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@ManagedBean
public class ArretDAO {

	@Inject
	EntityManager em;

}
