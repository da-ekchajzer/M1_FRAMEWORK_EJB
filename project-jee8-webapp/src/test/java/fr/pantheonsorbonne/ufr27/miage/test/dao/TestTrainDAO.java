package fr.pantheonsorbonne.ufr27.miage.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import fr.pantheonsorbonne.ufr27.miage.dao.ArretDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.GareDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.IncidentDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrainDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.VoyageDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.VoyageurDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.jpa.TrainSansResa;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestDatabase;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@EnableWeld
@TestInstance(Lifecycle.PER_CLASS)
public class TestTrainDAO {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator
			.from(VoyageurDAO.class, VoyageDAO.class, TrajetDAO.class, ItineraireDAO.class, IncidentDAO.class,
					ArretDAO.class, TrainDAO.class, GareDAO.class, TestPersistenceProducer.class, TestDatabase.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	TrainDAO trainDAO;
	@Inject
	TestDatabase testDatabase;

	@BeforeAll
	void setup() {
		em.getTransaction().begin();
		Train train1 = new TrainAvecResa("TGV");
		Train train2 = new TrainSansResa("TER");
		Train train3 = new TrainAvecResa("OUIGO");
		Train train4 = new TrainAvecResa("OUIGO");
		Train[] trains = { train1, train2, train3, train4 };
		for (Train t : trains) {
			em.persist(t);
		}
		em.getTransaction().commit();
	}

	@Test
	public void testGetAllTrains() {
		List<Train> trains = trainDAO.getAllTrains();
		assertEquals(4, trains.size());
	}

	@AfterAll
	void nettoyageDonnees() {
		testDatabase.clear();
	}

}