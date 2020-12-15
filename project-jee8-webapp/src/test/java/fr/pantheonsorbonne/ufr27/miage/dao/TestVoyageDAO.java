package fr.pantheonsorbonne.ufr27.miage.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import fr.pantheonsorbonne.ufr27.miage.n_dao.GareDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.VoyageDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyage;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
public class TestVoyageDAO {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator.from(VoyageDAO.class, GareDAO.class, TestPersistenceProducer.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	VoyageDAO voyageDAO;
	@Inject
	GareDAO gareDAO;

	
	

	@BeforeAll
	public void setup() {

		em.getTransaction().begin();
		
		String[] nomGares = { "Paris - Gare de Lyon", "Avignon-Centre", "Aix en Provence", "Marseille - St Charles",
				"Dijon-Ville", "Lyon - Pardieu", "Narbonne", "Sete", "Perpignan", "Paris - Montparnasse", "Tours",
				"Bordeaux - Saint-Jean", "Pessac", "Arcachon-Centre", "Nantes" };

		Map<String, Gare> gares = new HashMap<>();
		for (String nomGare : nomGares) {
			Gare g = new Gare(nomGare);
			gares.put(nomGare, g);
			em.persist(g);
		}
		
		
		Train train1 = new TrainAvecResa(1, "TGV");
		em.persist(train1);
		
		Itineraire itineraire1 = new Itineraire(train1);
		em.persist(itineraire1);
		Trajet trajet1 = new Trajet(gares.get("Paris - Gare de Lyon"), gares.get("Avignon-Centre"), itineraire1, 0);
		Trajet trajet2 = new Trajet(gares.get("Avignon-Centre"), gares.get("Aix en Provence"), itineraire1, 1);

		List<Trajet> voyageTrajet1 = new LinkedList<Trajet>();
		voyageTrajet1.add(trajet1);
		voyageTrajet1.add(trajet2);
		Voyage voyage1 = new Voyage(voyageTrajet1);
		em.persist(voyage1);
		
		em.getTransaction().commit();
	}


	@Test
	void testGetVoyagesByNomGareDepart() {
		List<Voyage> voyages =  voyageDAO.getVoyagesByNomGareDepart(gareDAO.getGaresByNom("Paris - Gare de Lyon").get(0));
		assertEquals(1,voyages.size());
		
	}
	
	@Test
	void testGetVoyagesByNomGareArrivee() {
		List<Voyage> voyages =  voyageDAO.getVoyagesByNomGareArrivee(gareDAO.getGaresByNom("Aix en Provence").get(0));
		assertEquals(1,voyages.size());
	}

	
	@Test
	void testGetAllVoyages() {
		List<Voyage> voyages = voyageDAO.getAllVoyages();
		assertEquals(1,voyages.size());
	}
	

}

