package fr.pantheonsorbonne.ufr27.miage.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Queue;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.persistence.EntityManager;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import fr.pantheonsorbonne.ufr27.miage.n_dao.ArretDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.TrainDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.VoyageDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.VoyageurDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jms.MessageGateway;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.TrainRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.TrajetRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.VoyageRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.VoyageurRepository;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceMajDecideur;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceMajExecuteur;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceUtilisateur;
import fr.pantheonsorbonne.ufr27.miage.n_service.impl.BDDFillerServiceImpl;
import fr.pantheonsorbonne.ufr27.miage.n_service.impl.ServiceMajDecideurImp;
import fr.pantheonsorbonne.ufr27.miage.n_service.impl.ServiceMajExecuteurImp;
import fr.pantheonsorbonne.ufr27.miage.n_service.impl.ServiceUtilisateurImp;
import fr.pantheonsorbonne.ufr27.miage.n_service.utils.Retard;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
public class TestServiceMajDecideur {


	@WeldSetup
	private WeldInitiator weld = WeldInitiator.from(ServiceMajDecideur.class, ServiceMajDecideurImp.class, 
			ServiceMajExecuteur.class, ServiceMajExecuteurImp.class, ServiceUtilisateur.class, ServiceUtilisateurImp.class, 
			TrainRepository.class, TrainDAO.class, ItineraireRepository.class, ItineraireDAO.class, TrajetRepository.class, 
			TrajetDAO.class, ArretRepository.class, ArretDAO.class, VoyageurRepository.class, VoyageurDAO.class, 
			VoyageRepository.class, VoyageDAO.class, MessageGateway.class, ConnectionFactory.class, 
			TestPersistenceProducer.class)
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
	void testDecideRetard() {
		
		
	}
	
	// TODO : suppr la méthode getItineraireByTrainEtEtat et n'utiliser que getAllItinerairesByTrainEtEtat (renvoyer une liste à chaque fois)
	// TODO : s'assurer qu'on ne peut pas avoir, pour le mm train, un itinéraire en cours et un en incident
	@Test
	void testGetRetardsItineraireEnCorespondance() {
		Train t = this.trainRepository.getTrainById(6);
		Itineraire it6 = itineraireRepository.getItineraireByTrainEtEtat(t.getId(), CodeEtatItinieraire.EN_ATTENTE);
		assertNotNull(it6);
		itineraireRepository.majEtatItineraire(it6, CodeEtatItinieraire.EN_COURS);
		it6.setArretActuel(it6.getArretsDesservis().get(0));
		this.serviceUtilisateur.initUtilisateursItineraire(t.getId());
		assertEquals(6, it6.getVoyageurs().size());
		Retard r1 = new Retard(it6, LocalTime.of(0,  30));
		assertEquals(1, this.serviceMajDecideur.getRetardsItineraireEnCorespondance(r1).size());
	}
	
	@Test
	void testFactoriseRetard() {
		Itineraire it1 = itineraireRepository.getItineraireByTrainEtEtat(this.trainRepository.getTrainById(1).getId(), CodeEtatItinieraire.EN_ATTENTE);
		Itineraire it2 = itineraireRepository.getItineraireByTrainEtEtat(this.trainRepository.getTrainById(2).getId(), CodeEtatItinieraire.EN_ATTENTE);

		Retard r1 = new Retard(it1, LocalTime.of(0,  10));
		Retard r2 = new Retard(it1, LocalTime.of(0,  20));
		Retard r3 = new Retard(it2, LocalTime.of(0,  15));

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