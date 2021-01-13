package fr.pantheonsorbonne.ufr27.miage.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import fr.pantheonsorbonne.ufr27.miage.dao.ArretDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.GareDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@EnableWeld
@TestInstance(Lifecycle.PER_CLASS)
public class TestArretDAO {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator.from(ArretDAO.class, GareDAO.class, TestPersistenceProducer.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	ArretDAO arretDAO;
	@Inject
	GareDAO gareDAO;
	
	private static List<Object> objectsToDelete; 

	@BeforeAll
	void setup() {
		objectsToDelete = new ArrayList<Object>();
	}
	
	@Test
	void testAvancerHeureArriveeEnGare() {		
		Gare gare = new Gare("Avignon-Centre");
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime hArriveeEnGare = now.plus(30, ChronoUnit.SECONDS);
		LocalDateTime hDepartDeGare = now.plus(40, ChronoUnit.SECONDS);
		Arret arret = new Arret(gare, hArriveeEnGare, hDepartDeGare);
		em.getTransaction().begin();
		em.persist(gare);
		em.persist(arret);
		em.getTransaction().commit();
		arretDAO.avancerHeureArriveeEnGare(arret, 7);
		assertEquals(arret.getHeureArriveeEnGare(), hArriveeEnGare.minusSeconds(7));
		
		objectsToDelete.add(gare);
		objectsToDelete.add(arret);
	}

	@Test
	void testAvancerHeureDepartDeGare() {
		Gare gare = new Gare("Avignon-Centre");
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime hArriveeEnGare = now.plus(30, ChronoUnit.SECONDS);
		LocalDateTime hDepartDeGare = now.plus(40, ChronoUnit.SECONDS);
		Arret arret = new Arret(gare, hArriveeEnGare, hDepartDeGare);
		em.getTransaction().begin();
		em.persist(gare);
		em.persist(arret);
		em.getTransaction().commit();
		arretDAO.avancerHeureDepartDeGare(arret, 10);
		assertEquals(arret.getHeureDepartDeGare(), hDepartDeGare.minusSeconds(10));
		
		objectsToDelete.add(gare);
		objectsToDelete.add(arret);
	}

	@Test
	void testRetarderHeureArriveeEnGare() {
		Gare gare = new Gare("Avignon-Centre");
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime hArriveeEnGare = now.plus(30, ChronoUnit.SECONDS);
		LocalDateTime hDepartDeGare = now.plus(40, ChronoUnit.SECONDS);
		Arret arret = new Arret(gare, hArriveeEnGare, hDepartDeGare);
		em.getTransaction().begin();
		em.persist(gare);
		em.persist(arret);
		em.getTransaction().commit();
		arretDAO.retarderHeureArriveeEnGare(arret, 7);
		assertEquals(arret.getHeureArriveeEnGare(), hArriveeEnGare.plusSeconds(7));
		
		objectsToDelete.add(gare);
		objectsToDelete.add(arret);
	}

	@Test
	void testRetardHeureDepartDeGare() {
		Gare gare = new Gare("Avignon-Centre");
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime hArriveeEnGare = now.plus(30, ChronoUnit.SECONDS);
		LocalDateTime hDepartDeGare = now.plus(40, ChronoUnit.SECONDS);
		Arret arret = new Arret(gare, hArriveeEnGare, hDepartDeGare);
		em.getTransaction().begin();
		em.persist(gare);
		em.persist(arret);
		em.getTransaction().commit();
		arretDAO.retardHeureDepartDeGare(arret, 10);
		assertEquals(arret.getHeureDepartDeGare(), hDepartDeGare.plusSeconds(10));
		
		objectsToDelete.add(gare);
		objectsToDelete.add(arret);
	}
	
	@AfterAll
	void nettoyageDonnees() {
		em.getTransaction().begin();
		for(Object o : objectsToDelete) {
			em.remove(o);
		}
		em.getTransaction().commit();
//		System.out.println(gareDAO.getAllGares().size() + " gares");
//		System.out.println(arretDAO.getAllArrets().size() + " arrêts");
	}

}