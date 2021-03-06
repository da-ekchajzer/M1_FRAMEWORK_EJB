package fr.pantheonsorbonne.ufr27.miage.test.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import fr.pantheonsorbonne.ufr27.miage.dao.ArretDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.GareDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.IncidentDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrainDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.VoyageDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.VoyageurDAO;
import fr.pantheonsorbonne.ufr27.miage.jms.MessageGateway;
import fr.pantheonsorbonne.ufr27.miage.jms.conf.JMSProducer;
import fr.pantheonsorbonne.ufr27.miage.jms.utils.BrokerUtils;
import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Incident;
import fr.pantheonsorbonne.ufr27.miage.jpa.Incident.CodeTypeIncident;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.GareRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.TrainRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.TrajetRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.VoyageRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.VoyageurRepository;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceMajDecideur;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceMajExecuteur;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceMajInfoGare;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceUtilisateur;
import fr.pantheonsorbonne.ufr27.miage.service.impl.BDDFillerServiceImpl;
import fr.pantheonsorbonne.ufr27.miage.service.impl.ServiceMajDecideurImp;
import fr.pantheonsorbonne.ufr27.miage.service.impl.ServiceMajExecuteurImp;
import fr.pantheonsorbonne.ufr27.miage.service.impl.ServiceMajInfoGareImp;
import fr.pantheonsorbonne.ufr27.miage.service.impl.ServiceUtilisateurImp;
import fr.pantheonsorbonne.ufr27.miage.service.utils.Retard;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestDatabase;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
@TestMethodOrder(OrderAnnotation.class)
public class TestServiceMajDecideur {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator
			.from(ServiceMajDecideur.class, ServiceMajDecideurImp.class, ServiceMajExecuteur.class,
					ServiceMajExecuteurImp.class, ServiceMajInfoGare.class, ServiceMajInfoGareImp.class,
					ServiceUtilisateur.class, ServiceUtilisateurImp.class, TrainRepository.class,
					ItineraireRepository.class, ArretRepository.class, VoyageurRepository.class, VoyageRepository.class,
					TrajetRepository.class, GareRepository.class, VoyageurDAO.class, VoyageDAO.class, TrajetDAO.class, ItineraireDAO.class,
					IncidentDAO.class, ArretDAO.class, TrainDAO.class, GareDAO.class, MessageGateway.class,
					JMSProducer.class, TestPersistenceProducer.class, TestDatabase.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	ServiceMajDecideur serviceMajDecideur;
	@Inject
	ServiceUtilisateur serviceUtilisateur;
	@Inject
	GareRepository gareRepository;
	@Inject
	TrainRepository trainRepository;
	@Inject
	ItineraireRepository itineraireRepository;
	@Inject
	TestDatabase testDatabase;

	@BeforeAll
	void initVarInDB() {
		new BDDFillerServiceImpl(em).fill();

		BrokerUtils.startBroker();
	}

	@Test
	@Order(1)
	void testDecideRetard() {
		Train t5 = trainRepository.getTrainByBusinessId(5);
		Itineraire it6 = itineraireRepository.getItineraireByTrainEtEtat(t5.getId(), CodeEtatItinieraire.EN_ATTENTE);
		assertNotNull(it6);
		itineraireRepository.majEtatItineraire(it6, CodeEtatItinieraire.EN_COURS);
		serviceUtilisateur.initUtilisateursItineraire(t5.getId());
		assertEquals(6, it6.getVoyageurs().size());
		LocalTime heureArriveeTerminusItineraire6 = it6.getArretsDesservis().get(it6.getArretsDesservis().size() - 1)
				.getHeureArriveeEnGare().toLocalTime();
		Train t6 = trainRepository.getTrainByBusinessId(6);
		Itineraire it7 = itineraireRepository.getItineraireByTrainEtEtat(t6.getId(), CodeEtatItinieraire.EN_ATTENTE);
		assertNotNull(it7);
		LocalTime heureArriveeTerminusItineraire7 = it7.getArretsDesservis().get(it7.getArretsDesservis().size() - 1)
				.getHeureArriveeEnGare().toLocalTime();
		serviceMajDecideur.decideRetard(new Retard(it6, LocalTime.of(0, 30)), true);
		heureArriveeTerminusItineraire6 = heureArriveeTerminusItineraire6.plusMinutes(30);
		heureArriveeTerminusItineraire7 = heureArriveeTerminusItineraire7.plusMinutes(30);
		assertEquals(heureArriveeTerminusItineraire6, it6.getArretsDesservis().get(it6.getArretsDesservis().size() - 1)
				.getHeureArriveeEnGare().toLocalTime());
		assertEquals(heureArriveeTerminusItineraire7, it7.getArretsDesservis().get(it7.getArretsDesservis().size() - 1)
				.getHeureArriveeEnGare().toLocalTime());

		it6.setArretActuel(it6.getArretsDesservis().get(1));
		LocalTime heureDepartPremierGareItineraire6 = it6.getArretsDesservis().get(1).getHeureArriveeEnGare()
				.toLocalTime();
		serviceMajDecideur.decideRetard(new Retard(it6, LocalTime.of(0, 15)), true);
		heureDepartPremierGareItineraire6 = heureDepartPremierGareItineraire6.plusMinutes(15);
		heureArriveeTerminusItineraire7 = heureArriveeTerminusItineraire7.plusMinutes(15);
		assertNotEquals(heureDepartPremierGareItineraire6,
				it6.getArretsDesservis().get(0).getHeureDepartDeGare().toLocalTime());
		assertEquals(heureArriveeTerminusItineraire7, it7.getArretsDesservis().get(it7.getArretsDesservis().size() - 1)
				.getHeureArriveeEnGare().toLocalTime());

	}

	@Test
	@Order(2)
	void testGetRetardsItineraireEnCorespondance() {
		Train t5 = trainRepository.getTrainByBusinessId(5);
		Itineraire it6 = itineraireRepository.getItineraireByTrainEtEtat(t5.getId(), CodeEtatItinieraire.EN_COURS);
		assertNotNull(it6);
		Retard r1 = new Retard(it6, LocalTime.of(0, 30));
		assertEquals(1, serviceMajDecideur.getRetardsItineraireEnCorespondance(r1).size());
	}

	@Test
	@Order(3)
	void testFactoriseRetard() {
		Itineraire it1 = itineraireRepository.getItineraireByTrainEtEtat(
				trainRepository.getTrainByBusinessId(1).getId(), CodeEtatItinieraire.EN_ATTENTE);
		Itineraire it2 = itineraireRepository.getItineraireByTrainEtEtat(
				trainRepository.getTrainByBusinessId(2).getId(), CodeEtatItinieraire.EN_ATTENTE);
		Retard r1 = new Retard(it1, LocalTime.of(0, 10));
		Retard r2 = new Retard(it1, LocalTime.of(0, 20));
		Retard r3 = new Retard(it2, LocalTime.of(0, 15));

		Queue<Retard> retards = new LinkedList<Retard>();
		retards.add(r1);
		retards.add(r2);
		retards.add(r3);
		int nbRetards = 3;
		serviceMajDecideur.factoriseRetard(retards);
		assertEquals(--nbRetards, retards.size());
		assertFalse(retards.contains(r1));
		assertTrue(retards.contains(r2));
		assertTrue(retards.contains(r3));
	}

	@Test
	@Order(4)
	void testSelectionnerUnItineraireDeSecours() {		
		// Pour ce test on ne veut pas que les itinéraires du service BddFillerService puissent interférer
		// (car ils sont paramétrés en seconde donc pourraient fausser les résultats)
		testDatabase.clear();
		
		LocalDateTime now = LocalDateTime.now();
		String[] nomGares = { "Paris - Gare de Lyon", "Avignon-Centre", "Aix en Provence", "Marseille - St Charles",
				"Dijon-Ville", "Lyon - Pardieu", "Narbonne", "Sete", "Perpignan", "Paris - Montparnasse", "Tours",
				"Bordeaux - Saint-Jean", "Pessac", "Arcachon-Centre", "Nantes", "Montpellier", "Cabries", "Le Creusot",
				"Lyon - Perrache" };

		Map<String, Gare> gares = new HashMap<>();
		for (String nomGare : nomGares) {
			Gare g = new Gare(nomGare);
			gares.put(nomGare, g);
			em.persist(g);
		}
		
		Arret arret1 = new Arret(gares.get("Paris - Montparnasse"), null, now.plusMinutes(15));
		Arret arret2 = new Arret(gares.get("Tours"), now.plusMinutes(20), now.plusMinutes(25));
		Arret arret3 = new Arret(gares.get("Bordeaux - Saint-Jean"), now.plusMinutes(30), null);
		
		Arret arret4 = new Arret(gares.get("Paris - Montparnasse"), null, now.plusMinutes(30));
		Arret arret5 = new Arret(gares.get("Bordeaux - Saint-Jean"), now.plusMinutes(35), null);
		
		Arret arret6 = new Arret(gares.get("Paris - Montparnasse"), null, now.plusMinutes(12));
		Arret arret7 = new Arret(gares.get("Avignon-Centre"), now.plusMinutes(20), now.plusMinutes(22));
		Arret arret8 = new Arret(gares.get("Bordeaux - Saint-Jean"), now.plusMinutes(32), null);
		
		Itineraire itAccidente = new Itineraire();
		itAccidente.addArret(arret1);
		itAccidente.addArret(arret2);
		itAccidente.addArret(arret3);
		
		Itineraire itSecours = new Itineraire();
		itSecours.addArret(arret4);
		itSecours.addArret(arret5);
		
		Itineraire itSecoursFini = new Itineraire();
		itSecoursFini.addArret(arret4);
		itSecoursFini.addArret(arret5);
		itSecoursFini.setEtat(CodeEtatItinieraire.FIN.getCode());
		
		Itineraire itSecoursPossible = new Itineraire();
		itSecoursPossible.addArret(arret6);
		itSecoursPossible.addArret(arret7);
		itSecoursPossible.addArret(arret8);
		
		em.getTransaction().begin();
		em.persist(arret1);
		em.persist(arret2);
		em.persist(arret3);
		em.persist(itAccidente);
		em.persist(arret4);
		em.persist(arret5);
		em.persist(itSecours);
		em.persist(itSecoursFini);
		em.persist(arret6);
		em.persist(arret7);
		em.persist(arret8);
		em.persist(itSecoursPossible);
		em.getTransaction().commit();
		
		// Cet itinéraire a les bonnes gares d'arrêt pour remplacer itAccidente mais il est déjà terminé
		assertNotEquals(itSecoursFini, this.serviceMajDecideur.selectionnerUnItineraireDeSecours(itAccidente));
		
		// Cet itinéraire a presque les bonnes gares d'arrêt pour remplacer itAccidente mais
		// il dessert une autre ville (Avignon) sur son chemin
		assertNotEquals(itSecoursPossible, this.serviceMajDecideur.selectionnerUnItineraireDeSecours(itAccidente));

		// Inutile de tester qu'un itinéraire n'ayant pas les mêmes départus & terminus ne sera pas choisi
		
		// L'itinéraire accidenté subit un retard trop important, il doit être remplacé
		assertEquals(itSecours, this.serviceMajDecideur.selectionnerUnItineraireDeSecours(itAccidente));
	}
	
	@AfterAll
	void nettoyageDonnees() {
		testDatabase.clear();
		BrokerUtils.stopBroker();
	}

}