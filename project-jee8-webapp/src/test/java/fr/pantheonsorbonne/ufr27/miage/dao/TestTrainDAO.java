package fr.pantheonsorbonne.ufr27.miage.dao;


import static org.junit.jupiter.api.Assertions.assertEquals;


import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.pantheonsorbonne.ufr27.miage.n_dao.TrainDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainSansResa;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@EnableWeld
public class TestTrainDAO {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator.from(TrainDAO.class, TestPersistenceProducer.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	TrainDAO trainDAO;

	@BeforeEach
	public void setup() {

		em.getTransaction().begin();

		Train train1 = new TrainAvecResa(1, "TGV");
		Train train2 = new TrainSansResa(2, "TER");
		Train train3 = new TrainAvecResa(3, "OUIGO");
		Train train4 = new TrainAvecResa(4, "OUIGO");
		Train train5 = new TrainSansResa(5, "TER");
		Train train6 = new TrainAvecResa(6, "TGV");
		Train train7 = new TrainSansResa(7, "TER");
		Train train8 = new TrainAvecResa(8, "TGV");
		Train train9 = new TrainAvecResa(9, "TGV");

		Train[] trains = { train1, train2, train3, train4, train5, train6, train7, train8, train9 };
		
		for (Train t : trains) {
			em.persist(t);
		}
		
		em.getTransaction().commit();
	}

	@Test
	void testGetTrainById() {
		assertEquals("OUIGO", trainDAO.getTrainById(4).getMarque());
	}

}