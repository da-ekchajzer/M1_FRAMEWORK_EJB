package fr.pantheonsorbonne.ufr27.miage.repository;

import static org.junit.Assert.assertNull;
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
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import fr.pantheonsorbonne.ufr27.miage.n_dao.ArretDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.GareDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.TrainDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.GareRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.TrainRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.TrajetRepository;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
@TestMethodOrder(OrderAnnotation.class)
public class TestItineraireRepository {
	
	private final static LocalDateTime HEURE_ACTUELLE = LocalDateTime.now();
	
	@WeldSetup
	private WeldInitiator weld = WeldInitiator.from(ItineraireRepository.class, ItineraireDAO.class, 
			TrainRepository.class, TrainDAO.class, TrajetRepository.class, TrajetDAO.class, 
			ArretRepository.class, ArretDAO.class, GareRepository.class, GareDAO.class, TestPersistenceProducer.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	TrainRepository trainRepository;
	@Inject
	ItineraireRepository itineraireRepository;
	@Inject
	GareRepository gareRepository;

	@BeforeAll
	void initVarInDB() {
		Train t = new TrainAvecResa(1, "Marque");
		
		Gare g1 = new Gare("Gare1");
		Gare g2 = new Gare("Gare2");
		Gare g3 = new Gare("Gare3");
		Gare g4 = new Gare("Gare4");
		Gare g6 = new Gare("Gare6");
		Gare g7 = new Gare("Gare7");
		Gare g9 = new Gare("Gare9");
		
		Itineraire i1 = new Itineraire(t);		
		i1.setEtat(CodeEtatItinieraire.EN_ATTENTE.getCode());
		
		Itineraire i2 = new Itineraire(t);
		i2.setEtat(CodeEtatItinieraire.EN_COURS.getCode());
		Arret arret1 = new Arret(g1, null, HEURE_ACTUELLE.plus(1, ChronoUnit.MINUTES));
		Arret arret2 = new Arret(g2, HEURE_ACTUELLE.plus(2, ChronoUnit.MINUTES), HEURE_ACTUELLE.plus(3, ChronoUnit.MINUTES));
		Arret arret3 = new Arret(g3, HEURE_ACTUELLE.plus(4, ChronoUnit.MINUTES), null);
		List<Arret> arretsI2 = new ArrayList<Arret>();
		arretsI2.add(arret1); arretsI2.add(arret2); arretsI2.add(arret3);
		i1.setArretsDesservis(arretsI2);
		i2.setArretsDesservis(arretsI2);
		i2.setArretActuel(arret1);
		
		Itineraire i3 = new Itineraire(t);
		i3.setEtat(CodeEtatItinieraire.EN_INCIDENT.getCode());
		Arret arret4 = new Arret(g4, null, HEURE_ACTUELLE.plus(1, ChronoUnit.MINUTES));
		Arret arret5 = new Arret(g2, HEURE_ACTUELLE.plus(2, ChronoUnit.MINUTES), HEURE_ACTUELLE.plus(3, ChronoUnit.MINUTES));
		Arret arret6 = new Arret(g6, HEURE_ACTUELLE.plus(4, ChronoUnit.MINUTES), null);
		List<Arret> arretsI3 = new ArrayList<Arret>();
		arretsI3.add(arret4); arretsI3.add(arret5); arretsI3.add(arret6);
		i3.setArretsDesservis(arretsI3);
		
		Itineraire i4 = new Itineraire(t);
		i4.setEtat(CodeEtatItinieraire.FIN.getCode());
		
		em.getTransaction().begin();
		em.persist(t);
		em.persist(g1);
		em.persist(g2);
		em.persist(g3);
		em.persist(g4);
		em.persist(g2);
		em.persist(g6);
		em.persist(g7);
		em.persist(g2);
		em.persist(g9);
		em.persist(arret1);
		em.persist(arret2);
		em.persist(arret3);
		em.persist(arret4);
		em.persist(arret5);
		em.persist(arret6);
		em.persist(i1);
		em.persist(i2);
		em.persist(i3);
		em.persist(i4);
		em.getTransaction().commit();
	}
	
	@Test
	@Order(1)
	void testRecupItineraireEnCoursOuLeProchain() {
		Train t = this.trainRepository.getTrainById(1);
		Itineraire i1 = this.itineraireRepository.getItineraireByTrainEtEtat(t.getId(), CodeEtatItinieraire.EN_COURS);
		assertEquals(i1,  this.itineraireRepository.recupItineraireEnCoursOuLeProchain(t.getId()));
	}
	
	@Test
	@Order(2)
	void testGetItineraireByTrainEtEtatNullSiPlusieursResultats() {
		Train t = this.trainRepository.getTrainById(1);
		Itineraire i1 = this.itineraireRepository.getItineraireByTrainEtEtat(t.getId(), CodeEtatItinieraire.EN_ATTENTE);
		this.itineraireRepository.majEtatItineraire(i1, CodeEtatItinieraire.EN_COURS);
		assertNull(this.itineraireRepository.getItineraireByTrainEtEtat(t.getId(), CodeEtatItinieraire.EN_COURS));
		this.itineraireRepository.majEtatItineraire(i1, CodeEtatItinieraire.FIN);
	}	
	
	@Test
	@Order(3)
	void testGetNextArretByIdTrainEtArret() {
		Train t = this.trainRepository.getTrainById(1);
		Itineraire i2 = this.itineraireRepository.getItineraireByTrainEtEtat(t.getId(), CodeEtatItinieraire.EN_COURS);
		// Renvoie le nextArret si on est pas à la fin de l'itinéraire
		i2.setArretActuel(this.itineraireRepository.getNextArret(t.getId(), i2.getArretActuel()));
		assertEquals("Gare2", i2.getArretActuel().getGare().getNom());
		assertEquals("Gare3", this.itineraireRepository.getNextArret(t.getId(), i2.getArretActuel()).getGare().getNom());
		// Renvoie null si on est au dernier arrêt (rien après)
		i2.setArretActuel(this.itineraireRepository.getNextArret(t.getId(), i2.getArretActuel()));
		assertEquals("Gare3", i2.getArretActuel().getGare().getNom());
		assertEquals(null, this.itineraireRepository.getNextArret(t.getId(), i2.getArretActuel()));
	}
	
	@Test
	@Order(4)
	void testGetAllNextArrets() {
		Train t = this.trainRepository.getTrainById(1);
		Itineraire i2 = this.itineraireRepository.getItineraireByTrainEtEtat(t.getId(), CodeEtatItinieraire.EN_COURS);
		// Si on est au dernier arrêt, il n'y en a plus après
		assertEquals(0, this.itineraireRepository.getAllNextArrets(i2).size());
	}
	
	@Test
	@Order(5)
	void testGetItinerairesEnCoursOuEnIncidentByGare() {
		Gare g = this.gareRepository.getGaresByNom("Gare2").get(0);
		assertEquals(2, this.itineraireRepository.getItinerairesEnCoursOuEnIncidentByGare(g).size());
	}
	
	@Test
	@Order(6)
	void testGetAllItinerairesByGare() {
		Gare g = this.gareRepository.getGaresByNom("Gare2").get(0);
		assertEquals(3, this.itineraireRepository.getAllItinerairesByGare(g).size());
	}

	@Test
	@Order(7)
	void testGetAllItinerairesAtLeastIn() {
		// TODO
	}
	
	@Test
	@Order(8)
	void testSupprimerArretDansUnItineraire() {
		Train t = this.trainRepository.getTrainById(1);
		Itineraire i2 = this.itineraireRepository.getItineraireByTrainEtEtat(t.getId(), CodeEtatItinieraire.EN_COURS);
		int nbArrets = i2.getArretsDesservis().size();
		i2 = this.itineraireRepository.supprimerArretDansUnItineraire(t.getId(), i2.getArretsDesservis().get(1));
		assertEquals(nbArrets - 1,  i2.getArretsDesservis().size());
	}
}