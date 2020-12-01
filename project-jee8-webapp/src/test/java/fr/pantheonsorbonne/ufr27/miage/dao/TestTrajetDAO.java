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
import org.junit.jupiter.api.BeforeEach;
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
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainSansResa;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
public class TestTrajetDAO {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator.from(TrajetDAO.class, TestPersistenceProducer.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	TrajetDAO trajetDAO;
	ItineraireDAO itineraireDAO;
	GareDAO gareDAO;

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

		// ---------------------------------Trains
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
		for (Train t : trains)
			em.persist(t);

// ---------------------------------Arrêts

		Arret arret1 = new Arret(gares.get("Paris - Gare de Lyon"), null, LocalDateTime.now());
		Arret arret2 = new Arret(gares.get("Avignon-Centre"), LocalDateTime.now().plus(1, ChronoUnit.MINUTES),
				LocalDateTime.now().plus(1, ChronoUnit.MINUTES).plus(30, ChronoUnit.SECONDS));
		Arret arret3 = new Arret(gares.get("Aix en Provence"), LocalDateTime.now().plus(3, ChronoUnit.MINUTES),
				LocalDateTime.now().plus(3, ChronoUnit.MINUTES).plus(1, ChronoUnit.MINUTES));
		Arret arret4 = new Arret(gares.get("Marseille - St Charles"), LocalDateTime.now().plus(5, ChronoUnit.MINUTES),
				null);

		Arret arret1_bis = new Arret(gares.get("Perpignan"), null, LocalDateTime.now());
		Arret arret2_bis = new Arret(gares.get("Sete"), LocalDateTime.now().plus(1, ChronoUnit.HOURS),
				LocalDateTime.now().plus(1, ChronoUnit.HOURS).plus(10, ChronoUnit.MINUTES));
		Arret arret3_bis = new Arret(gares.get("Narbonne"), LocalDateTime.now().plus(2, ChronoUnit.HOURS),
				LocalDateTime.now().plus(2, ChronoUnit.HOURS).plus(10, ChronoUnit.MINUTES));
		Arret arret4_bis = new Arret(gares.get("Marseille - St Charles"), LocalDateTime.now().plus(3, ChronoUnit.HOURS),
				null);

		Arret arret5 = new Arret(gares.get("Marseille - St Charles"), null,
				LocalDateTime.now().plus(4, ChronoUnit.HOURS));
		Arret arret6 = new Arret(gares.get("Dijon-Ville"), LocalDateTime.now().plus(5, ChronoUnit.HOURS),
				LocalDateTime.now().plus(5, ChronoUnit.HOURS).plus(5, ChronoUnit.MINUTES));
		Arret arret7 = new Arret(gares.get("Lyon - Pardieu"), LocalDateTime.now().plus(6, ChronoUnit.HOURS), null);

		Arret arret5_bis = new Arret(gares.get("Lyon - Pardieu"), null, LocalDateTime.now());
		Arret arret6_bis = new Arret(gares.get("Dijon-Ville"), LocalDateTime.now().plus(1, ChronoUnit.HOURS),
				LocalDateTime.now().plus(1, ChronoUnit.HOURS).plus(5, ChronoUnit.MINUTES));
		Arret arret7_bis = new Arret(gares.get("Marseille - St Charles"), LocalDateTime.now().plus(2, ChronoUnit.HOURS),
				null);

		Arret arret8 = new Arret(gares.get("Marseille - St Charles"), null,
				LocalDateTime.now().plus(3, ChronoUnit.HOURS));
		Arret arret9 = new Arret(gares.get("Narbonne"), LocalDateTime.now().plus(4, ChronoUnit.HOURS),
				LocalDateTime.now().plus(4, ChronoUnit.HOURS).plus(10, ChronoUnit.MINUTES));
		Arret arret10 = new Arret(gares.get("Sete"), LocalDateTime.now().plus(5, ChronoUnit.HOURS),
				LocalDateTime.now().plus(5, ChronoUnit.HOURS).plus(10, ChronoUnit.MINUTES));
		Arret arret11 = new Arret(gares.get("Perpignan"), LocalDateTime.now().plus(6, ChronoUnit.HOURS), null);

		Arret arret12 = new Arret(gares.get("Paris - Montparnasse"), null, LocalDateTime.now());
		Arret arret13 = new Arret(gares.get("Tours"), LocalDateTime.now().plus(1, ChronoUnit.HOURS),
				LocalDateTime.now().plus(1, ChronoUnit.HOURS).plus(5, ChronoUnit.MINUTES));
		Arret arret14 = new Arret(gares.get("Bordeaux - Saint-Jean"), LocalDateTime.now().plus(2, ChronoUnit.HOURS),
				null);

		Arret arret15 = new Arret(gares.get("Bordeaux - Saint-Jean"), null,
				LocalDateTime.now().plus(3, ChronoUnit.HOURS));
		Arret arret16 = new Arret(gares.get("Pessac"), LocalDateTime.now().plus(4, ChronoUnit.HOURS),
				LocalDateTime.now().plus(4, ChronoUnit.HOURS).plus(10, ChronoUnit.MINUTES));
		Arret arret17 = new Arret(gares.get("Arcachon-Centre"), LocalDateTime.now().plus(5, ChronoUnit.HOURS), null);

		Arret arret18 = new Arret(gares.get("Nantes"), null, LocalDateTime.now());
		Arret arret19 = new Arret(gares.get("Paris - Montparnasse"), LocalDateTime.now().plus(1, ChronoUnit.HOURS),
				null);

		Arret arret18_bis = new Arret(gares.get("Paris - Montparnasse"), null,
				LocalDateTime.now().plus(2, ChronoUnit.HOURS));
		Arret arret19_bis = new Arret(gares.get("Bordeaux - Saint-Jean"), LocalDateTime.now().plus(4, ChronoUnit.HOURS),
				null);

		Arret[] arrets = { arret1, arret2, arret3, arret4, arret1_bis, arret2_bis, arret3_bis, arret4_bis, arret5,
				arret6, arret7, arret5_bis, arret6_bis, arret7_bis, arret8, arret9, arret10, arret11, arret12, arret13,
				arret14, arret15, arret16, arret17, arret18, arret19, arret18_bis, arret19_bis };

		for (Arret a : arrets)
			em.persist(a);

		// --------------------------------- Itinéraires

		Itineraire itineraire1 = new Itineraire(train1);
		itineraire1.addArret(arret1);
		itineraire1.addArret(arret2);
		itineraire1.addArret(arret3);
		itineraire1.addArret(arret4);

		Itineraire itineraire2 = new Itineraire(train2);
		itineraire2.addArret(arret1_bis);
		itineraire2.addArret(arret2_bis);
		itineraire2.addArret(arret3_bis);
		itineraire2.addArret(arret4_bis);

		Itineraire itineraire3 = new Itineraire(train3);
		itineraire3.addArret(arret5);
		itineraire3.addArret(arret6);
		itineraire3.addArret(arret7);

		Itineraire itineraire4 = new Itineraire(train4);
		itineraire4.addArret(arret5_bis);
		itineraire4.addArret(arret6_bis);
		itineraire4.addArret(arret7_bis);

		Itineraire itineraire5 = new Itineraire(train5);
		itineraire5.addArret(arret8);
		itineraire5.addArret(arret9);
		itineraire5.addArret(arret10);
		itineraire5.addArret(arret11);

		Itineraire itineraire6 = new Itineraire(train6);
		itineraire6.addArret(arret12);
		itineraire6.addArret(arret13);
		itineraire6.addArret(arret14);

		Itineraire itineraire7 = new Itineraire(train7);
		itineraire7.addArret(arret15);
		itineraire7.addArret(arret16);
		itineraire7.addArret(arret17);

		Itineraire itineraire8 = new Itineraire(train8);
		itineraire8.addArret(arret18);
		itineraire8.addArret(arret19);

		Itineraire itineraire9 = new Itineraire(train9);
		itineraire9.addArret(arret18_bis);
		itineraire9.addArret(arret19_bis);

		Itineraire[] itineraires = { itineraire1, itineraire2, itineraire3, itineraire4, itineraire5, itineraire6,
				itineraire7, itineraire8, itineraire9 };

		for (Itineraire i : itineraires)
			em.persist(i);

		// --------------------------------- Trajets

		Trajet trajet1 = new Trajet(gares.get("Paris - Gare de Lyon"), gares.get("Avignon-Centre"), itineraire1, 0);
		Trajet trajet2 = new Trajet(gares.get("Avignon-Centre"), gares.get("Aix en Provence"), itineraire1, 1);
		Trajet trajet3 = new Trajet(gares.get("Aix en Provence"), gares.get("Marseille - St Charles"), itineraire1, 2);

		Trajet trajet1_bis = new Trajet(gares.get("Perpignan"), gares.get("Sete"), itineraire2, 0);
		Trajet trajet2_bis = new Trajet(gares.get("Sete"), gares.get("Narbonne"), itineraire2, 1);
		Trajet trajet3_bis = new Trajet(gares.get("Narbonne"), gares.get("Marseille - St Charles"), itineraire2, 2);

		// Changement
		Trajet trajet4 = new Trajet(gares.get("Marseille - St Charles"), gares.get("Dijon-Ville"), itineraire3, 0);
		Trajet trajet5 = new Trajet(gares.get("Dijon-Ville"), gares.get("Lyon - Pardieu"), itineraire3, 1);

		Trajet trajet4_bis = new Trajet(gares.get("Lyon - Pardieu"), gares.get("Dijon-Ville"), itineraire4, 0);
		Trajet trajet5_bis = new Trajet(gares.get("Dijon-Ville"), gares.get("Marseille - St Charles"), itineraire4, 1);

		// Changement
		Trajet trajet6 = new Trajet(gares.get("Marseille - St Charles"), gares.get("Narbonne"), itineraire5, 0);
		Trajet trajet7 = new Trajet(gares.get("Narbonne"), gares.get("Sete"), itineraire5, 1);
		Trajet trajet8 = new Trajet(gares.get("Sete"), gares.get("Perpignan"), itineraire5, 2);

		Trajet trajet9 = new Trajet(gares.get("Paris - Montparnasse"), gares.get("Tours"), itineraire6, 0);
		Trajet trajet10 = new Trajet(gares.get("Tours"), gares.get("Bordeaux - Saint-Jean"), itineraire6, 1);

		// Changement
		Trajet trajet11 = new Trajet(gares.get("Bordeaux - Saint-Jean"), gares.get("Pessac"), itineraire7, 0);
		Trajet trajet12 = new Trajet(gares.get("Pessac"), gares.get("Arcachon-Centre"), itineraire7, 1);

		Trajet trajet13 = new Trajet(gares.get("Nantes"), gares.get("Paris - Montparnasse"), itineraire8, 0);

		// Changement
		Trajet trajet14 = new Trajet(gares.get("Paris - Montparnasse"), gares.get("Bordeaux - Saint-Jean"), itineraire9,
				0);

		Trajet[] trajets = { trajet1, trajet2, trajet3, trajet4, trajet5, trajet6, trajet7, trajet8, trajet9, trajet10,
				trajet11, trajet12, trajet13, trajet14 };

		for (Trajet t : trajets)
			em.persist(t);

		em.getTransaction().commit();
	}

//	Ne fonctionne pas
//	@Test
//	void testTrajetByItineraire() {
//		List<Trajet> trajets = trajetDAO.getTrajetsByItineraire(itineraireDAO.getItineraireById(1));
//		assertEquals(1, trajets.size());
//	}

//	Ne fonctionne pas
//	@Test
//	void testTrajetNomGareDeDepart() {
//		List<Trajet> trajets = trajetDAO.getTrajetsByNomGareArrivee((Gare) gareDAO.getGaresByNom("Paris - Gare de Lyon"));
//		assertEquals(1,trajets.size());
//	}

}