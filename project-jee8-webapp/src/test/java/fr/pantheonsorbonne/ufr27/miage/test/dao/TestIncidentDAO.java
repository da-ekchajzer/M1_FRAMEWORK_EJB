package fr.pantheonsorbonne.ufr27.miage.test.dao;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

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
import fr.pantheonsorbonne.ufr27.miage.jpa.Incident.CodeEtatIncident;
import fr.pantheonsorbonne.ufr27.miage.jpa.Incident.CodeTypeIncident;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestDatabase;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

//Toujours mettre l'annotation @TestInstance(Lifecycle.PER_CLASS) quand on utilise @BeforeAll
@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
public class TestIncidentDAO {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator
			.from(VoyageurDAO.class, VoyageDAO.class, TrajetDAO.class, ItineraireDAO.class, IncidentDAO.class,
					ArretDAO.class, TrainDAO.class, GareDAO.class, TestPersistenceProducer.class, TestDatabase.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;

	@Inject
	IncidentDAO incidentDAO;
	@Inject
	ItineraireDAO itineraireDAO;
	@Inject
	TestDatabase testDatabase;

	@BeforeAll
	public void setup() {
		em.getTransaction().begin();

		Incident incident1 = new Incident(CodeTypeIncident.ANIMAL_SUR_VOIE.getCode(), "I1");
		Incident incident2 = new Incident(CodeTypeIncident.PERSONNE_SUR_VOIE.getCode(), "I2");

		em.persist(incident1);
		em.persist(incident2);

		em.getTransaction().commit();
	}

	@Test
	void testGetAllIncidents() {
		List<Incident> incidents = incidentDAO.getAllIncidents();
		assertEquals(2, incidents.size());
	}

	@Test
	void testGetNbIncident() {
		assertEquals(2, incidentDAO.getNbIncidents());
	}

	@Test
	void testGetIncidentByBusinessId() {
		assertEquals(CodeEtatIncident.EN_COURS.getCode(), incidentDAO.getIncidentByBusinessId("I1").getEtat());
	}

	@Test
	void testAjouterIncidentEnBDD() {
		Incident incident3 = new Incident();
		int nbIncidents = incidentDAO.getNbIncidents();
		incidentDAO.ajouterIncidentEnBD(incident3);
		assertEquals(nbIncidents + 1, incidentDAO.getNbIncidents());
	}

	@Test
	void testMajEtatIncidentEnBDD() {
		incidentDAO.majEtatIncidentEnBD(incidentDAO.getIncidentByBusinessId("I1"), CodeEtatIncident.RESOLU);
		assertEquals(CodeEtatIncident.RESOLU.getCode(), incidentDAO.getIncidentByBusinessId("I1").getEtat());
	}

	@Test
	void testAssocierIncidentItineraire() {
		Itineraire itineraire1 = new Itineraire();
		em.getTransaction().begin();
		em.persist(itineraire1);
		em.getTransaction().commit();
		assertEquals(null, itineraire1.getIncident());
		incidentDAO.associerIncidentItineraire(itineraire1, incidentDAO.getIncidentByBusinessId("I1"));
		assertEquals(CodeTypeIncident.ANIMAL_SUR_VOIE.getCode(), itineraire1.getIncident().getTypeIncident());
	}

	@AfterAll
	void nettoyageDonnees() {
		testDatabase.clear();
	}

}