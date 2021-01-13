package fr.pantheonsorbonne.ufr27.miage.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import fr.pantheonsorbonne.ufr27.miage.dao.TrainDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.jpa.TrainSansResa;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@EnableWeld
@TestInstance(Lifecycle.PER_CLASS)
public class TestTrainDAO {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator.from(TrainDAO.class, TestPersistenceProducer.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	TrainDAO trainDAO;

	private static List<Object> objectsToDelete; 

	@BeforeAll
	void setup() {
		objectsToDelete = new ArrayList<Object>();

		em.getTransaction().begin();
		Train train1 = new TrainAvecResa("TGV");
		Train train2 = new TrainSansResa("TER");
		Train train3 = new TrainAvecResa("OUIGO");
		Train train4 = new TrainAvecResa("OUIGO");
		Train[] trains = { train1, train2, train3, train4 };
		for (Train t : trains) {
			objectsToDelete.add(t);
			em.persist(t);
		}
		em.getTransaction().commit();
	}

//	@Test
//	void testGetTrainByBusinessId() {
//		List<Train> trains = this.trainDAO.getAllTrains();
//		assertEquals(4, trains.size());
//		Train train = trainDAO.getTrainByBusinessId("T4");
//		assertEquals("OUIGO", train.getMarque());
//	}
	
	@Test
	public void testGetAllTrains() {
		assertEquals(4, this.trainDAO.getAllTrains().size());
		em.getTransaction().begin();
		for(Object o : objectsToDelete) {
			em.remove(o);
		}
		em.getTransaction().commit();
		assertEquals(0, this.trainDAO.getAllTrains().size());
	}
	
	@AfterAll
	void nettoyageDonnees() {
		System.out.println(trainDAO.getAllTrains().size() + " trains");
	}

}