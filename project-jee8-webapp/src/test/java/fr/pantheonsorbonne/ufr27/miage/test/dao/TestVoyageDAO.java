package fr.pantheonsorbonne.ufr27.miage.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

import fr.pantheonsorbonne.ufr27.miage.dao.GareDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrainDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.VoyageDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.jpa.Voyage;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
public class TestVoyageDAO {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator.from(VoyageDAO.class, 
			GareDAO.class, ItineraireDAO.class, TrajetDAO.class, TrainDAO.class,
			TestPersistenceProducer.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	VoyageDAO voyageDAO;
	@Inject
	GareDAO gareDAO;
	@Inject
	ItineraireDAO itineraireDAO;
	@Inject
	TrajetDAO trajetDAO;
	@Inject
	TrainDAO trainDAO;
	
	private static List<Object> objectsToDelete; 

	@BeforeAll
	public void setup() {
		objectsToDelete = new ArrayList<Object>();

		System.out.println(this.gareDAO.getAllGares().size());
		System.out.println(this.voyageDAO.getAllVoyages().size());
		System.out.println(this.trainDAO.getAllTrains().size());

		String[] nomGares = { "Paris - Gare de Lyon", "Avignon-Centre", "Aix en Provence", "Marseille - St Charles",
				"Dijon-Ville", "Lyon - Pardieu", "Narbonne", "Sete", "Perpignan", "Paris - Montparnasse", "Tours",
				"Bordeaux - Saint-Jean", "Pessac", "Arcachon-Centre", "Nantes" };
		Map<String, Gare> gares = new HashMap<>();
		Train train1 = new TrainAvecResa("TGV");
		Itineraire itineraire1 = new Itineraire(train1);
		
		em.getTransaction().begin();
		for (String nomGare : nomGares) {
			Gare g = new Gare(nomGare);
			gares.put(nomGare, g);
			em.persist(g);
			objectsToDelete.add(g);
		}
		em.persist(train1);
		em.persist(itineraire1);
		em.getTransaction().commit();
		objectsToDelete.add(train1);
		objectsToDelete.add(itineraire1);

		
		Trajet trajet1 = new Trajet(gares.get("Paris - Gare de Lyon"), gares.get("Avignon-Centre"), itineraire1, 0);
		Trajet trajet2 = new Trajet(gares.get("Avignon-Centre"), gares.get("Aix en Provence"), itineraire1, 1);
		List<Trajet> voyageTrajet1 = new LinkedList<Trajet>();
		voyageTrajet1.add(trajet1);
		voyageTrajet1.add(trajet2);
		Voyage voyage1 = new Voyage(voyageTrajet1);
		
		em.getTransaction().begin();
		em.persist(trajet1);
		em.persist(trajet2);
		em.persist(voyage1);
		em.getTransaction().commit();
		
		objectsToDelete.add(trajet1);
		objectsToDelete.add(trajet2);
		objectsToDelete.add(voyage1);
	}

	@Test
	void testGetVoyagesByNomGareDepart() {
		List<Voyage> voyages = voyageDAO
				.getVoyagesByNomGareDepart(gareDAO.getGaresByNom("Paris - Gare de Lyon").get(0));
		assertEquals(1, voyages.size());

	}

	@Test
	void testGetVoyagesByNomGareArrivee() {
		List<Voyage> voyages = voyageDAO.getVoyagesByNomGareArrivee(gareDAO.getGaresByNom("Aix en Provence").get(0));
		assertEquals(1, voyages.size());
	}

	@Test
	void testGetAllVoyages() {
		List<Voyage> voyages = voyageDAO.getAllVoyages();
		assertEquals(1, voyages.size());
	}
	
	@AfterAll
	void nettoyageDonnees() {
		em.getTransaction().begin();
//		for(Voyage v : voyageDAO.getAllVoyages()) {
//			em.remove(v);
//		}
//		for(Trajet t : trajetDAO.getAllTrajets()) {
//			em.remove(t);
//		}
//		for(Itineraire i : itineraireDAO.getAllItineraires()) {
//			em.remove(i);
//		}
//		for(Train t : trainDAO.getAllTrains()) {
//			em.remove(t);
//		}
//		for(Gare g : gareDAO.getAllGares()) {
//			em.remove(g);
//		}
		for(Object o : objectsToDelete) {
			em.remove(o);
		}
		em.getTransaction().commit();
		
	}

}
