package fr.pantheonsorbonne.ufr27.miage.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
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
import fr.pantheonsorbonne.ufr27.miage.n_dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
public class TestTrajetDAO {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator
			.from(TrajetDAO.class, GareDAO.class, ItineraireDAO.class, TestPersistenceProducer.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	TrajetDAO trajetDAO;
	@Inject
	GareDAO gareDAO;
	@Inject
	ItineraireDAO itineraireDAO;

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
		Arret arret2 = new Arret(gares.get("Avignon-Centre"), LocalDateTime.now().plus(1, ChronoUnit.MINUTES),
				LocalDateTime.now().plus(1, ChronoUnit.MINUTES).plus(30, ChronoUnit.SECONDS));
		Arret arret3 = new Arret(gares.get("Aix en Provence"), LocalDateTime.now().plus(3, ChronoUnit.MINUTES),
				LocalDateTime.now().plus(3, ChronoUnit.MINUTES).plus(1, ChronoUnit.MINUTES));
		Arret arret4 = new Arret(gares.get("Marseille - St Charles"), LocalDateTime.now().plus(5, ChronoUnit.MINUTES),
				null);

		Arret[] arrets = { arret1, arret2, arret3, arret4 };

		for (Arret a : arrets)
			em.persist(a);

		// --------------------------------- Itinéraires

		Itineraire itineraire1 = new Itineraire(train1);
		itineraire1.addArret(arret1);
		itineraire1.addArret(arret2);
		itineraire1.addArret(arret3);
		itineraire1.addArret(arret4);

		em.persist(itineraire1);

		Itineraire[] itineraires = { itineraire1 };

		for (Itineraire i : itineraires)
			em.persist(i);

		// --------------------------------- Trajets

		Trajet trajet1 = new Trajet(gares.get("Paris - Gare de Lyon"), gares.get("Avignon-Centre"), itineraire1, 0);
		Trajet trajet2 = new Trajet(gares.get("Avignon-Centre"), gares.get("Aix en Provence"), itineraire1, 1);
		Trajet trajet3 = new Trajet(gares.get("Aix en Provence"), gares.get("Marseille - St Charles"), itineraire1, 2);

		Trajet[] trajets = { trajet1, trajet2, trajet3 };

		for (Trajet t : trajets)
			em.persist(t);

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

	@Test
	void testGetTrajetsNomGareDeDepart() {
		List<Trajet> trajets = trajetDAO
				.getTrajetsByNomGareDeDepart(gareDAO.getGaresByNom("Paris - Gare de Lyon").get(0));
		assertEquals(1, trajets.size());
	}

	@Test
	void testTrajetNomGareArrivee() {
		List<Trajet> trajets = trajetDAO
				.getTrajetsByNomGareArrivee(gareDAO.getGaresByNom("Marseille - St Charles").get(0));
		assertEquals(1, trajets.size());
	}

	@Test
	void testDeleteTrajet() {
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
		trajetDAO.deleteTrajet(trajet3);
		trajets = trajetDAO.getTrajetsByItineraire(itineraireDAO.getItineraireById(itineraire.getId()));
		assertEquals(2, trajets.size());

	}

}