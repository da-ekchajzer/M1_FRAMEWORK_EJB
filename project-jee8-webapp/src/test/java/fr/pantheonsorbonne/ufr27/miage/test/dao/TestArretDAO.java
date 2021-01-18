package fr.pantheonsorbonne.ufr27.miage.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
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
import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestDatabase;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@EnableWeld
@TestInstance(Lifecycle.PER_CLASS)
public class TestArretDAO {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator
			.from(VoyageurDAO.class, VoyageDAO.class, TrajetDAO.class, ItineraireDAO.class, IncidentDAO.class,
					ArretDAO.class, TrainDAO.class, GareDAO.class, TestPersistenceProducer.class, TestDatabase.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	ArretDAO arretDAO;
	@Inject
	GareDAO gareDAO;
	@Inject
	TestDatabase testDatabase;

	@Test
	void testAvancerHeureArriveeEnGare() {
		Gare gare = new Gare("Avignon-Centre");
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime hArriveeEnGare = now.plusSeconds(30);
		LocalDateTime hDepartDeGare = now.plusSeconds(40);
		Arret arret = new Arret(gare, hArriveeEnGare, hDepartDeGare);
		em.getTransaction().begin();
		em.persist(gare);
		em.persist(arret);
		em.getTransaction().commit();
		arretDAO.avancerHeureArriveeEnGare(arret, 7);
		assertEquals(arret.getHeureArriveeEnGare(), hArriveeEnGare.minusSeconds(7));
	}

	@Test
	void testAvancerHeureDepartDeGare() {
		Gare gare = new Gare("Avignon-Centre");
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime hArriveeEnGare = now.plusSeconds(30);
		LocalDateTime hDepartDeGare = now.plusSeconds(40);
		Arret arret = new Arret(gare, hArriveeEnGare, hDepartDeGare);
		em.getTransaction().begin();
		em.persist(gare);
		em.persist(arret);
		em.getTransaction().commit();
		arretDAO.avancerHeureDepartDeGare(arret, 10);
	}

	@Test
	void testRetarderHeureArriveeEnGare() {
		Gare gare = new Gare("Avignon-Centre");
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime hArriveeEnGare = now.plusSeconds(30);
		LocalDateTime hDepartDeGare = now.plusSeconds(40);
		Arret arret = new Arret(gare, hArriveeEnGare, hDepartDeGare);
		em.getTransaction().begin();
		em.persist(gare);
		em.persist(arret);
		em.getTransaction().commit();
		arretDAO.retarderHeureArriveeEnGare(arret, 7);
		assertEquals(arret.getHeureArriveeEnGare(), hArriveeEnGare.plusSeconds(7));
	}

	@Test
	void testRetardHeureDepartDeGare() {
		Gare gare = new Gare("Avignon-Centre");
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime hArriveeEnGare = now.plusSeconds(30);
		LocalDateTime hDepartDeGare = now.plusSeconds(40);
		Arret arret = new Arret(gare, hArriveeEnGare, hDepartDeGare);
		em.getTransaction().begin();
		em.persist(gare);
		em.persist(arret);
		em.getTransaction().commit();
		arretDAO.retardHeureDepartDeGare(arret, 10);
		assertEquals(arret.getHeureDepartDeGare(), hDepartDeGare.plusSeconds(10));
	}

	@AfterAll
	void nettoyageDonnees() {
		testDatabase.clear();
	}

}