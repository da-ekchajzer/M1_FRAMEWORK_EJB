package fr.pantheonsorbonne.ufr27.miage.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.pantheonsorbonne.ufr27.miage.n_dao.GareDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestUtils;

public class TestGareDAO {

	//static EntityManager em;
	static GareDAO gareDAO;

//	@BeforeClass
//	public static void setup() {
//		em = TestUtils.startServer();
//		em.getTransaction().begin();
//
//		String[] nomGares = { "Paris - Gare de Lyon", "Avignon-Centre", "Aix en Provence", "Marseille - St Charles",
//				"Dijon-Ville", "Lyon - Pardieu", "Narbonne", "Sete", "Perpignan", "Paris - Montparnasse", "Tours",
//				"Bordeaux - Saint-Jean", "Pessac", "Arcachon-Centre", "Nantes" };
//
//		Map<String, Gare> gares = new HashMap<>();
//		for (String nomGare : nomGares) {
//			Gare g = new Gare(nomGare);
//			gares.put(nomGare, g);
//			em.persist(g);
//		}
//		em.getTransaction().commit();
//		
//		gareDAO = new GareDAO();
//	}
	
	@Test
	public void testGetGaresByNom() {
		GareDAO gareDAO = new GareDAO();
		List<Gare> gares = gareDAO.getGaresByNom("Avignon-Centre");
		assertEquals(1, gares.size());
		assertEquals("Avignon-Centre", gares.get(0).getNom());
	}
	
//	
//	@AfterClass
//	public static void end() {
//		TestUtils.stopServer();
//	}
}
