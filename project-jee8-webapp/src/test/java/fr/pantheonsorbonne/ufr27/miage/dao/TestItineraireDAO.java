package fr.pantheonsorbonne.ufr27.miage.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import fr.pantheonsorbonne.ufr27.miage.n_dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.ItineraireDAO.MulitpleResultsNotExpectedException;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
public class TestItineraireDAO {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator.from(ItineraireDAO.class, TestPersistenceProducer.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	ItineraireDAO itineraireDAO;


	@Test
	void testGetItineraireById() {
		Itineraire itineraire = new Itineraire();
		em.getTransaction().begin();
		em.persist(itineraire);
		em.getTransaction().commit();
		assertEquals(itineraire, itineraireDAO.getItineraireById(itineraire.getId()));
	}

	@Test
	void testGetItineraireByTrainEtEtat() throws MulitpleResultsNotExpectedException {
		Train train = new TrainAvecResa(1, "TGV");
		Itineraire itineraire = new Itineraire(train);
		em.getTransaction().begin();
		em.persist(train);
		em.persist(itineraire);
		em.getTransaction().commit();
		itineraireDAO.getItineraireByTrainEtEtat(train.getId(), CodeEtatItinieraire.EN_ATTENTE);
		assertEquals(CodeEtatItinieraire.EN_ATTENTE.getCode(), itineraire.getEtat());
		
		Itineraire itineraire2 = new Itineraire(train);
		em.getTransaction().begin();
		em.persist(itineraire2);
		em.getTransaction().commit();
		Exception exception = assertThrows(MulitpleResultsNotExpectedException.class, () -> {
			itineraireDAO.getItineraireByTrainEtEtat(train.getId(), CodeEtatItinieraire.EN_ATTENTE);
		});
		assertEquals("Expected only one 'Itineraire'", exception.getMessage());
	}

	@Test
	void testGetAllItinerairesByTrainEtEtat() throws MulitpleResultsNotExpectedException {
		Train train2 = new TrainAvecResa(2, "OUIGO");
		Train train3 = new TrainAvecResa(3, "TGV");
		Train train4 = new TrainAvecResa(4, "TER");
		Itineraire itineraire1 = new Itineraire(train2);
		Itineraire itineraire2 = new Itineraire(train3);
		Itineraire itineraire3 = new Itineraire(train3);
		em.getTransaction().begin();
		em.persist(train2);
		em.persist(train3);
		em.persist(train4);
		em.persist(itineraire1);
		em.persist(itineraire2);
		em.persist(itineraire3);
		em.getTransaction().commit();
		List<Itineraire> itineraires = itineraireDAO.getAllItinerairesByTrainEtEtat(train2.getId(),
				CodeEtatItinieraire.EN_ATTENTE);
		assertEquals(1, itineraires.size());
		List<Itineraire> itineraires2 = itineraireDAO.getAllItinerairesByTrainEtEtat(train4.getId(),
				CodeEtatItinieraire.EN_ATTENTE);
		assertEquals(0, itineraires2.size());
	
	}

	@Test
	void testMajEtatItineraire() {
		Train train5 = new TrainAvecResa(5, "OUIGO");
		Itineraire itineraire = new Itineraire(train5);
		em.getTransaction().begin();
		em.persist(train5);
		em.persist(itineraire);
		em.getTransaction().commit();
		assertEquals(itineraire.getEtat(), CodeEtatItinieraire.EN_ATTENTE.getCode());
		itineraireDAO.majEtatItineraire(itineraire, CodeEtatItinieraire.FIN);
		assertEquals(CodeEtatItinieraire.FIN.getCode(), itineraire.getEtat());
	}

	@Test
	void testMajArretActuel() {
		Arret arret = new Arret();
		Itineraire itineraire = new Itineraire();
		em.getTransaction().begin();
		em.persist(arret);
		em.persist(itineraire);
		em.getTransaction().commit();
		assertEquals(itineraire.getArretActuel(), null);
		itineraireDAO.majArretActuel(itineraire, arret);
		assertEquals(itineraire.getArretActuel(), arret);
	}

	/*
	@Test
	void testSupprimerArretDansUnItineraire() {
		Arret arret = new Arret();
		Itineraire itineraire = new Itineraire();
		em.getTransaction().begin();
		em.persist(arret);
		em.persist(itineraire);
		em.getTransaction().commit();
		itineraire.addArret(arret);
		assertEquals(itineraire.getArretsDesservis().get(0), arret);
		itineraireDAO.supprimerArretDansUnItineraire(itineraire, arret);
		assertEquals(itineraire.getArretsDesservis().size(), 0);
	}
	*/

	@Test
	void testRetarderTrain() {
		LocalTime retard = LocalTime.of(0, 0, 5);
		Arret arret1 = new Arret(null,LocalDateTime.now(),LocalDateTime.now().plus(10, ChronoUnit.SECONDS));
		Arret arret2 = new Arret(null,LocalDateTime.now().plus(20, ChronoUnit.SECONDS),LocalDateTime.now().plus(30, ChronoUnit.SECONDS));
		Arret arret3 = new Arret(null,LocalDateTime.now().plus(40, ChronoUnit.SECONDS),null);
		Itineraire itineraire = new Itineraire();
		itineraire.addArret(arret1);
		itineraire.addArret(arret2);
		itineraire.addArret(arret3);
		em.getTransaction().begin();
		em.persist(arret1);
		em.persist(arret2);
		em.persist(arret3);
		em.persist(itineraire);
		em.getTransaction().commit();
		LocalDateTime d1 = arret1.getHeureDepartDeGare();
		LocalDateTime a2 = arret2.getHeureArriveeEnGare();
		LocalDateTime d2 = arret2.getHeureDepartDeGare();
		LocalDateTime a3 = arret3.getHeureArriveeEnGare();
		itineraireDAO.retarderTrain(retard, arret2, itineraire);
		assertEquals(d1,arret1.getHeureDepartDeGare());	
		assertEquals(a2.plus(5, ChronoUnit.SECONDS),arret2.getHeureArriveeEnGare());
		assertEquals(d2.plus(5, ChronoUnit.SECONDS),arret2.getHeureDepartDeGare());	
		assertEquals(a3.plus(5, ChronoUnit.SECONDS),arret3.getHeureArriveeEnGare());
	}
	

	// La méthode ajouterUnArretDansUnItineraire sera déplacée donc pas testée ici

}