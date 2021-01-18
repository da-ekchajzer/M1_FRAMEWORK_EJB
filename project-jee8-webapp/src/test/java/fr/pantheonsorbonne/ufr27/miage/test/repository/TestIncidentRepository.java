package fr.pantheonsorbonne.ufr27.miage.test.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import fr.pantheonsorbonne.ufr27.miage.dao.ArretDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.GareDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.IncidentDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrainDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.VoyageDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.VoyageurDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.Incident;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.jpa.Incident.CodeEtatIncident;
import fr.pantheonsorbonne.ufr27.miage.jpa.Incident.CodeTypeIncident;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.IncidentRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.TrainRepository;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestDatabase;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
@TestMethodOrder(OrderAnnotation.class)
public class TestIncidentRepository {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator
			.from(TrainRepository.class, ItineraireRepository.class, ArretRepository.class, IncidentRepository.class,
					VoyageurDAO.class, VoyageDAO.class, TrajetDAO.class, ItineraireDAO.class, IncidentDAO.class,
					ArretDAO.class, TrainDAO.class, GareDAO.class, TestPersistenceProducer.class, TestDatabase.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	TrainRepository trainRepository;
	@Inject
	ItineraireRepository itineraireRepository;
	@Inject
	IncidentRepository incidentRepository;
	@Inject
	TestDatabase testDatabase;

	@BeforeAll
	void initVarInDB() {
		Train train1 = new TrainAvecResa("Marque");
		Itineraire itineraire1 = new Itineraire();
		itineraire1.setTrain(train1);
		itineraire1.setEtat(CodeEtatItinieraire.EN_COURS.getCode());

		em.getTransaction().begin();
		em.persist(train1);
		em.persist(itineraire1);
		em.getTransaction().commit();
	}

	@Test
	@Order(1)
	void testCreerIncident() {
		// Train t = trainRepository.getTrainByBusinessId(1);
		List<Train> trains = trainRepository.getAllTrains();
		System.out.println(trains.get(0).getBusinessId());
		Train t = trains.get(0);
		assertNotNull(t);
		Incident i = new Incident();
		i.setTypeIncident(CodeTypeIncident.ANIMAL_SUR_VOIE.getCode());
		i.setEtat(CodeEtatIncident.EN_COURS.getCode());
		assertNotNull(i);

		assertEquals(0, incidentRepository.getAllIncidents().size());
		i = incidentRepository.creerIncident(t.getId(), i);
		assertEquals(1, incidentRepository.getAllIncidents().size());

		Itineraire it = itineraireRepository.getItineraireByTrainEtEtat(t.getId(), CodeEtatItinieraire.EN_INCIDENT);
		assertEquals(it.getIncident(), i);
		assertEquals(CodeTypeIncident.ANIMAL_SUR_VOIE.getCode(), it.getIncident().getTypeIncident());
	}

	@Test
	@Order(2)
	void testGetIncidentByIdTrain() {
		Train t = trainRepository.getTrainByBusinessId(1);
		assertEquals(CodeTypeIncident.ANIMAL_SUR_VOIE.getCode(),
				incidentRepository.getIncidentByIdTrain(t.getId()).getTypeIncident());
	}

	@Test
	@Order(3)
	void testUpdateEtatIncident() {
		Train t = trainRepository.getTrainByBusinessId(1);
		incidentRepository.majEtatIncident(incidentRepository.getIncidentByIdTrain(t.getId()), CodeEtatIncident.RESOLU);
		assertEquals(CodeEtatIncident.RESOLU.getCode(), incidentRepository.getIncidentByIdTrain(t.getId()).getEtat());
	}

	@AfterAll
	void nettoyageDonnees() {
		testDatabase.clear();
	}

}