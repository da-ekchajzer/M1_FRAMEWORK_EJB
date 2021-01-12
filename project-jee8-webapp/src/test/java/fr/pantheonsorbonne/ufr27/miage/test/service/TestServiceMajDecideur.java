package fr.pantheonsorbonne.ufr27.miage.test.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Queue;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import fr.pantheonsorbonne.ufr27.miage.dao.ArretDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrainDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.VoyageDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.VoyageurDAO;
import fr.pantheonsorbonne.ufr27.miage.jms.MessageGateway;
import fr.pantheonsorbonne.ufr27.miage.jms.conf.JMSProducer;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.TrainRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.TrajetRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.VoyageRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.VoyageurRepository;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceMajDecideur;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceMajExecuteur;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceUtilisateur;
import fr.pantheonsorbonne.ufr27.miage.service.impl.BDDFillerServiceImpl;
import fr.pantheonsorbonne.ufr27.miage.service.impl.ServiceMajDecideurImp;
import fr.pantheonsorbonne.ufr27.miage.service.impl.ServiceMajExecuteurImp;
import fr.pantheonsorbonne.ufr27.miage.service.impl.ServiceUtilisateurImp;
import fr.pantheonsorbonne.ufr27.miage.service.utils.Retard;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
@TestMethodOrder(OrderAnnotation.class)
public class TestServiceMajDecideur {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator
			.from(ServiceMajDecideur.class, ServiceMajDecideurImp.class, ServiceMajExecuteur.class,
					ServiceMajExecuteurImp.class, ServiceUtilisateur.class, ServiceUtilisateurImp.class,
					TrainRepository.class, TrainDAO.class, ItineraireRepository.class, ItineraireDAO.class,
					TrajetRepository.class, TrajetDAO.class, ArretRepository.class, ArretDAO.class,
					VoyageurRepository.class, VoyageurDAO.class, VoyageRepository.class, VoyageDAO.class,
					MessageGateway.class, JMSProducer.class, TestPersistenceProducer.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	ServiceMajExecuteur serviceMajExecuteur;
	@Inject
	ServiceMajDecideur serviceMajDecideur;
	@Inject
	ServiceUtilisateur serviceUtilisateur;
	@Inject
	TrainRepository trainRepository;
	@Inject
	ArretRepository arretRepository;
	@Inject
	ItineraireRepository itineraireRepository;

	@BeforeAll
	void initVarInDB() {
		new BDDFillerServiceImpl(em).fill();
	}

	@Test
	@Order(1)
	void testDecideRetard() {
		Train t6 = this.trainRepository.getTrainByBusinessId(6);
		Itineraire it6 = itineraireRepository.getItineraireByTrainEtEtat(t6.getId(), CodeEtatItinieraire.EN_ATTENTE);
		assertNotNull(it6);
		itineraireRepository.majEtatItineraire(it6, CodeEtatItinieraire.EN_COURS);
		it6.setArretActuel(it6.getArretsDesservis().get(0));
		this.serviceUtilisateur.initUtilisateursItineraire(t6.getId());
		assertEquals(6, it6.getVoyageurs().size());
		LocalTime heureArriveeTerminusItineraire6 = it6.getArretsDesservis().get(it6.getArretsDesservis().size() - 1)
				.getHeureArriveeEnGare().toLocalTime();
		Train t7 = this.trainRepository.getTrainByBusinessId(7);
		Itineraire it7 = itineraireRepository.getItineraireByTrainEtEtat(t7.getId(), CodeEtatItinieraire.EN_ATTENTE);
		assertNotNull(it7);
		LocalTime heureArriveeTerminusItineraire7 = it7.getArretsDesservis().get(it7.getArretsDesservis().size() - 1)
				.getHeureArriveeEnGare().toLocalTime();
		this.serviceMajDecideur.decideRetard(new Retard(it6, LocalTime.of(0, 30)), true);
		heureArriveeTerminusItineraire6 = heureArriveeTerminusItineraire6.plusMinutes(30);
		heureArriveeTerminusItineraire7 = heureArriveeTerminusItineraire7.plusMinutes(30);
		assertEquals(heureArriveeTerminusItineraire6, it6.getArretsDesservis().get(it6.getArretsDesservis().size() - 1)
				.getHeureArriveeEnGare().toLocalTime());
		assertEquals(heureArriveeTerminusItineraire7, it7.getArretsDesservis().get(it7.getArretsDesservis().size() - 1)
				.getHeureArriveeEnGare().toLocalTime());

		it6.setArretActuel(it6.getArretsDesservis().get(1));
		LocalTime heureDepartPremierGareItineraire6 = it6.getArretsDesservis().get(1).getHeureArriveeEnGare()
				.toLocalTime();
		this.serviceMajDecideur.decideRetard(new Retard(it6, LocalTime.of(0, 15)), true);
		heureDepartPremierGareItineraire6 = heureDepartPremierGareItineraire6.plusMinutes(15);
		heureArriveeTerminusItineraire7 = heureArriveeTerminusItineraire7.plusMinutes(15);
		assertNotEquals(heureDepartPremierGareItineraire6,
				it6.getArretsDesservis().get(0).getHeureDepartDeGare().toLocalTime());
		assertEquals(heureArriveeTerminusItineraire7, it7.getArretsDesservis().get(it7.getArretsDesservis().size() - 1)
				.getHeureArriveeEnGare().toLocalTime());

	}

	// TODO : suppr la méthode getItineraireByTrainEtEtat et n'utiliser que
	// getAllItinerairesByTrainEtEtat (renvoyer une liste à chaque fois)
	// TODO : s'assurer qu'on ne peut pas avoir, pour le mm train, un itinéraire en
	// cours et un en incident
	@Test
	@Order(2)
	void testGetRetardsItineraireEnCorespondance() {
		Train t = this.trainRepository.getTrainByBusinessId(6);
		Itineraire it6 = itineraireRepository.getItineraireByTrainEtEtat(t.getId(), CodeEtatItinieraire.EN_COURS);
		assertNotNull(it6);
		Retard r1 = new Retard(it6, LocalTime.of(0, 30));
		assertEquals(1, this.serviceMajDecideur.getRetardsItineraireEnCorespondance(r1).size());
	}

	@Test
	@Order(3)
	void testFactoriseRetard() {
		Itineraire it1 = itineraireRepository.getItineraireByTrainEtEtat(this.trainRepository.getTrainByBusinessId(1).getId(),
				CodeEtatItinieraire.EN_ATTENTE);
		Itineraire it2 = itineraireRepository.getItineraireByTrainEtEtat(this.trainRepository.getTrainByBusinessId(2).getId(),
				CodeEtatItinieraire.EN_ATTENTE);
		Retard r1 = new Retard(it1, LocalTime.of(0, 10));
		Retard r2 = new Retard(it1, LocalTime.of(0, 20));
		Retard r3 = new Retard(it2, LocalTime.of(0, 15));

		Queue<Retard> retards = new LinkedList<Retard>();
		retards.add(r1);
		retards.add(r2);
		retards.add(r3);
		int nbRetards = 3;
		this.serviceMajDecideur.factoriseRetard(retards);
		assertEquals(--nbRetards, retards.size());
		assertFalse(retards.contains(r1));
		assertTrue(retards.contains(r2));
		assertTrue(retards.contains(r3));
	}
}