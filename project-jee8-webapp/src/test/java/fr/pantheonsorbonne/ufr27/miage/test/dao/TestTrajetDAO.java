package fr.pantheonsorbonne.ufr27.miage.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.HashMap;
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
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestDatabase;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
public class TestTrajetDAO {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator
			.from(VoyageurDAO.class, VoyageDAO.class, TrajetDAO.class, ItineraireDAO.class, IncidentDAO.class,
					ArretDAO.class, TrainDAO.class, GareDAO.class, TestPersistenceProducer.class, TestDatabase.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	TrajetDAO trajetDAO;
	@Inject
	GareDAO gareDAO;
	@Inject
	TrainDAO trainDAO;
	@Inject
	ItineraireDAO itineraireDAO;
	@Inject
	ArretDAO arretDAO;
	@Inject
	TestDatabase testDatabase;

	@BeforeAll
	public void setup() {
		em.getTransaction().begin();

		// --------------------------------- Gares
		String[] nomGares = { "Paris - Gare de Lyon", "Avignon-Centre", "Aix en Provence", "Marseille - St Charles",
				"Dijon-Ville", "Lyon - Pardieu", "Narbonne", "Sete", "Perpignan", "Paris - Montparnasse", "Tours",
				"Bordeaux - Saint-Jean", "Pessac", "Arcachon-Centre", "Nantes" };

		Map<String, Gare> gares = new HashMap<>();
		for (String nomGare : nomGares) {
			Gare g = new Gare(nomGare);
			gares.put(nomGare, g);
			em.persist(g);
		}

		// --------------------------------- Train
		Train train1 = new TrainAvecResa("TGV");
		em.persist(train1);

		// --------------------------------- Arrêts

		Arret arret1 = new Arret(gares.get("Paris - Gare de Lyon"), null, LocalDateTime.now());
		Arret arret2 = new Arret(gares.get("Avignon-Centre"), LocalDateTime.now().plusMinutes(1),
				LocalDateTime.now().plusMinutes(1).plusSeconds(30));
		Arret arret3 = new Arret(gares.get("Aix en Provence"), LocalDateTime.now().plusMinutes(3),
				LocalDateTime.now().plusMinutes(3).plusMinutes(1));
		Arret arret4 = new Arret(gares.get("Marseille - St Charles"), LocalDateTime.now().plusMinutes(5), null);

		Arret[] arrets = { arret1, arret2, arret3, arret4 };

		for (Arret a : arrets) {
			em.persist(a);
		}

		// --------------------------------- Itinéraires

		Itineraire itineraire1 = new Itineraire(train1);
		itineraire1.addArret(arret1);
		itineraire1.addArret(arret2);
		itineraire1.addArret(arret3);
		itineraire1.addArret(arret4);

		em.persist(itineraire1);

		// --------------------------------- Trajets

		Trajet trajet1 = new Trajet(gares.get("Paris - Gare de Lyon"), gares.get("Avignon-Centre"), itineraire1, 0);
		Trajet trajet2 = new Trajet(gares.get("Avignon-Centre"), gares.get("Aix en Provence"), itineraire1, 1);
		Trajet trajet3 = new Trajet(gares.get("Aix en Provence"), gares.get("Marseille - St Charles"), itineraire1, 2);

		Trajet[] trajets = { trajet1, trajet2, trajet3 };

		for (Trajet t : trajets) {
			em.persist(t);
		}

		em.getTransaction().commit();
	}

	@Test
	void testGetTrajetsByItineraire() {
		em.getTransaction().begin();
		Itineraire itineraire = new Itineraire();
		em.persist(itineraire);
		Trajet trajet1 = new Trajet();
		Trajet trajet2 = new Trajet();
		Trajet trajet3 = new Trajet();
		trajet1.setItineraire(itineraire);
		trajet2.setItineraire(itineraire);
		trajet3.setItineraire(itineraire);
		em.persist(trajet1);
		em.persist(trajet2);
		em.persist(trajet3);
		em.getTransaction().commit();
		List<Trajet> trajets = trajetDAO.getTrajetsByItineraire(itineraireDAO.getItineraireById(itineraire.getId()));
		assertEquals(3, trajets.size());
	}

	@AfterAll
	void nettoyageDonnees() {
		testDatabase.clear();
	}

}