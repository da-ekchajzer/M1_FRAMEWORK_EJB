package fr.pantheonsorbonne.ufr27.miage.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import fr.pantheonsorbonne.ufr27.miage.dao.ArretDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.GareDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.IncidentDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.ItineraireDAO.MulitpleResultsNotExpectedException;
import fr.pantheonsorbonne.ufr27.miage.dao.TrainDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.VoyageDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.VoyageurDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestDatabase;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
public class TestItineraireDAO {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator
			.from(VoyageurDAO.class, VoyageDAO.class, TrajetDAO.class, ItineraireDAO.class, IncidentDAO.class,
					ArretDAO.class, TrainDAO.class, GareDAO.class, TestPersistenceProducer.class, TestDatabase.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	ItineraireDAO itineraireDAO;
	@Inject
	ArretDAO arretDAO;
	@Inject
	TrainDAO trainDAO;
	@Inject
	TestDatabase testDatabase;

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
		Train train = new TrainAvecResa("TGV");
		Itineraire itineraire = new Itineraire(train);
		Train train2 = new TrainAvecResa("TER");
		em.getTransaction().begin();
		em.persist(train);
		em.persist(train2);
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
		assertEquals(null, itineraireDAO.getItineraireByTrainEtEtat(train2.getId(), CodeEtatItinieraire.EN_ATTENTE));
	}

	@Test
	void testGetAllItinerairesByTrainEtEtat() throws MulitpleResultsNotExpectedException {
		Train train2 = new TrainAvecResa("OUIGO");
		Train train3 = new TrainAvecResa("TGV");
		Train train4 = new TrainAvecResa("TER");
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
		Train train5 = new TrainAvecResa("OUIGO");
		Itineraire itineraire = new Itineraire(train5);
		Itineraire itineraire2 = new Itineraire();
		em.getTransaction().begin();
		em.persist(train5);
		em.persist(itineraire);
		em.persist(itineraire2);
		em.getTransaction().commit();
		assertEquals(itineraire.getEtat(), CodeEtatItinieraire.EN_ATTENTE.getCode());
		itineraireDAO.majEtatItineraire(itineraire, CodeEtatItinieraire.FIN);
		assertEquals(CodeEtatItinieraire.FIN.getCode(), itineraire.getEtat());
		itineraire2.setEtat(CodeEtatItinieraire.EN_INCIDENT.getCode());
		itineraireDAO.majEtatItineraire(itineraire2, CodeEtatItinieraire.EN_COURS);
		assertEquals(null, itineraire2.getIncident());
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

	@Test
	void testAjouterArretEnCoursItineraire() {
		Arret a1 = new Arret(null, null, LocalDateTime.now());
		Arret a2 = new Arret(null, LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2));
		Arret a3 = new Arret(null, LocalDateTime.now().plusMinutes(5), null);

		Itineraire i1 = new Itineraire();
		i1.addArret(a1);
		i1.addArret(a2);
		i1.addArret(a3);

		// A ajouter au milieu de l'itinéraire
		Arret arret1ToAdd = new Arret(null, LocalDateTime.now().plusMinutes(3), LocalDateTime.now().plusMinutes(4));
		// A ajouter comme terminus (normalement ne doit pas fonctionner)
		Arret arret2ToAdd = new Arret(null, LocalDateTime.now().plusMinutes(10), null);

		em.getTransaction().begin();
		em.persist(a1);
		em.persist(a2);
		em.persist(a3);
		em.persist(arret1ToAdd);
		em.persist(arret2ToAdd);
		em.persist(i1);
		em.getTransaction().commit();

		assertEquals(3, i1.getArretsDesservis().size());
		itineraireDAO.ajouterUnArretEnCoursItineraire(i1, arret1ToAdd);
		assertEquals(4, i1.getArretsDesservis().size());
		itineraireDAO.ajouterUnArretEnCoursItineraire(i1, arret2ToAdd);
		assertEquals(4, i1.getArretsDesservis().size());
	}

	@Test
	void testAjouterArretEnBoutItineraire() {
		Arret a1 = new Arret(null, null, LocalDateTime.now());
		Arret a2 = new Arret(null, LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2));
		Arret a3 = new Arret(null, LocalDateTime.now().plusMinutes(5), null);

		Itineraire i1 = new Itineraire();
		i1.addArret(a1);
		i1.addArret(a2);
		i1.addArret(a3);

		// A ajouter au milieu de l'itinéraire (normalement ne doit pas fonctionner)
		Arret arret1ToAdd = new Arret(null, LocalDateTime.now().plusMinutes(3), LocalDateTime.now().plusMinutes(4));
		// A ajouter comme terminus
		Arret arret2ToAdd = new Arret(null, LocalDateTime.now().plusMinutes(10), null);
		// Ajouter comme departus
		Arret arret3ToAdd = new Arret(null, null, LocalDateTime.now().minusMinutes(1));

		em.getTransaction().begin();
		em.persist(a1);
		em.persist(a2);
		em.persist(a3);
		em.persist(arret1ToAdd);
		em.persist(arret2ToAdd);
		em.persist(arret3ToAdd);
		em.persist(i1);
		em.getTransaction().commit();

		assertEquals(3, i1.getArretsDesservis().size());
		// Ajouter en tant que terminus (fonctionne pas car arret1ToAdd n'est pas un
		// terminus)
		itineraireDAO.ajouterUnArretEnBoutItineraire(i1, arret1ToAdd,
				arret1ToAdd.getHeureArriveeEnGare().minusSeconds(30));
		assertEquals(3, i1.getArretsDesservis().size());
		// Ajouter en tant que terminus
		itineraireDAO.ajouterUnArretEnBoutItineraire(i1, arret2ToAdd,
				arret2ToAdd.getHeureArriveeEnGare().minusSeconds(30));
		assertEquals(4, i1.getArretsDesservis().size());
		// Ajouter en tant que departus
		itineraireDAO.ajouterUnArretEnBoutItineraire(i1, arret3ToAdd,
				arret3ToAdd.getHeureDepartDeGare().minusSeconds(30));
		assertEquals(5, i1.getArretsDesservis().size());
	}

	@Test
	void testGetAllItinerairesByEtat() {
		Itineraire itineraire1 = new Itineraire();
		Itineraire itineraire2 = new Itineraire();
		Itineraire itineraire3 = new Itineraire();
		itineraire1.setEtat(CodeEtatItinieraire.EN_ATTENTE.getCode());
		itineraire2.setEtat(CodeEtatItinieraire.EN_ATTENTE.getCode());
		itineraire3.setEtat(CodeEtatItinieraire.EN_COURS.getCode());
		em.getTransaction().begin();
		em.persist(itineraire1);
		em.persist(itineraire2);
		em.persist(itineraire3);
		em.getTransaction().commit();
		List<Itineraire> listItinerairesAttente = itineraireDAO.getAllItinerairesByEtat(CodeEtatItinieraire.EN_ATTENTE);
		assertEquals(2, listItinerairesAttente.size());
		List<Itineraire> listItinerairesEnCours = itineraireDAO.getAllItinerairesByEtat(CodeEtatItinieraire.EN_COURS);
		assertEquals(1, listItinerairesEnCours.size());
	}

	@AfterAll
	void nettoyageDonnees() {
		testDatabase.clear();
	}

}