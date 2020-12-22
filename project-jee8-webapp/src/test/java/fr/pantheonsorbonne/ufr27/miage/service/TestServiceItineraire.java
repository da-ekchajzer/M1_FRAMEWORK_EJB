package fr.pantheonsorbonne.ufr27.miage.service;

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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ArretJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ItineraireJAXB;
import fr.pantheonsorbonne.ufr27.miage.n_dao.ArretDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.TrainDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.VoyageDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.VoyageurDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.TrainRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.TrajetRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.VoyageRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.VoyageurRepository;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceItineraire;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceUtilisateur;
import fr.pantheonsorbonne.ufr27.miage.n_service.impl.ServiceItineraireImp;
import fr.pantheonsorbonne.ufr27.miage.n_service.impl.ServiceUtilisateurImp;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
public class TestServiceItineraire {

	private final static LocalDateTime HEURE_ACTUELLE = LocalDateTime.now();
	
	@WeldSetup
	private WeldInitiator weld = WeldInitiator.from(ServiceItineraire.class, ServiceItineraireImp.class, 
			ServiceUtilisateur.class, ServiceUtilisateurImp.class, ItineraireRepository.class, ItineraireDAO.class, 
			ArretRepository.class, ArretDAO.class, TrajetRepository.class, TrajetDAO.class, 
			TrainRepository.class, TrainDAO.class, VoyageurRepository.class, VoyageurDAO.class, 
			VoyageRepository.class, VoyageDAO.class, TestPersistenceProducer.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	ServiceItineraire serviceItineraire;
	@Inject
	TrainRepository trainRepository;
	@Inject
	ItineraireRepository itineraireRepository;


	@BeforeAll
	void initVarInDB() {
		Train train1 = new TrainAvecResa(1, "Marque");
		
		Gare g1 = new Gare("Gare1");
		Gare g2 = new Gare("Gare2");
		Gare g3 = new Gare("Gare3");
		
		Arret arret1 = new Arret(g1, HEURE_ACTUELLE, HEURE_ACTUELLE.plus(1, ChronoUnit.MINUTES));
		Arret arret2 = new Arret(g2, HEURE_ACTUELLE.plus(2, ChronoUnit.MINUTES), HEURE_ACTUELLE.plus(3, ChronoUnit.MINUTES));
		Arret arret3 = new Arret(g3, HEURE_ACTUELLE.plus(4, ChronoUnit.MINUTES), HEURE_ACTUELLE.plus(5, ChronoUnit.MINUTES));
		List<Arret> arretsItineraire1 = new ArrayList<Arret>();
		arretsItineraire1.add(arret1); arretsItineraire1.add(arret2); arretsItineraire1.add(arret3);
		
		Itineraire itineraire1 = new Itineraire();
		itineraire1.setTrain(train1);
		itineraire1.setEtat(CodeEtatItinieraire.EN_COURS.getCode());
		itineraire1.setArretsDesservis(arretsItineraire1);
		itineraire1.setArretActuel(arret1);
		
		em.getTransaction().begin();
		em.persist(train1);
		em.persist(g1);
		em.persist(g2);
		em.persist(g3);
		em.persist(arret1);
		em.persist(arret2);
		em.persist(arret3);
		em.persist(itineraire1);
		em.getTransaction().commit();
	}
	
	@Test
	void testGetItineraireJaxbByIdTrain() {
		Train t = this.trainRepository.getTrainById(1);
		ItineraireJAXB itineraireJAXB = this.serviceItineraire.getItineraire(t.getId());
		assertEquals(itineraireJAXB.getEtatItineraire(), CodeEtatItinieraire.EN_COURS.getCode());
		assertEquals(itineraireJAXB.getArrets().get(0).getGare(), "Gare1");
		assertEquals(itineraireJAXB.getArrets().size(), 3);
	}
	
	@Test
	void testMajItineraire() {
		Train t = this.trainRepository.getTrainById(1);
		ArretJAXB arretJAXB = new ArretJAXB();
		arretJAXB.setGare("Gare2");
		assertEquals(true,  this.serviceItineraire.majItineraire(t.getId(),  arretJAXB));
		Itineraire it = this.itineraireRepository.getItineraireByTrainEtEtat(t.getId(), CodeEtatItinieraire.EN_COURS);
		assertEquals("Gare3", it.getArretActuel().getGare().getNom());
		// TODO : tester la partie sur les montées/descentes de voyageurs ?
	}

}