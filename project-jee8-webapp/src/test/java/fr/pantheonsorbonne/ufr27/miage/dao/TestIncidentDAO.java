package fr.pantheonsorbonne.ufr27.miage.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import fr.pantheonsorbonne.ufr27.miage.n_dao.IncidentDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

//Toujours mettre l'annotation @TestInstance(Lifecycle.PER_CLASS) quand on utilise @BeforeAll
@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
public class TestIncidentDAO {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator.from(IncidentDAO.class, TestPersistenceProducer.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	IncidentDAO incidentDAO;

	
	@BeforeAll
	public void setup() {
		em.getTransaction().begin();
		
		Incident incident1 = new Incident();
		Incident incident2 = new Incident();
		Incident incident3 = new Incident();
		
		Itineraire itineraire1 = new Itineraire();
		
		incident1.setTypeIncident(1);
		incident1.setEtat(1);

		em.persist(incident1);
		em.persist(incident2);
		em.persist(itineraire1);
		
		em.getTransaction().commit();
	}

	@Test
	void testGetAllIncidents() {

		List<Incident> incidents = incidentDAO.getAllIncidents();
		assertEquals(2, incidents.size());
	}
	
	@Test
	void testGetNbIncident() {
		assertEquals(2,incidentDAO.getNbIncidents());
	}
	
	@Test
	void testGetIncidentById() {
		assertEquals(1, incidentDAO.getIncidentById(1).getEtat());	
	}
	
//	Ne fonctionne pas
//	@Test
//	void testAjouterIncidentEnBDD() {
//		incidentDAO.ajouterIncidentEnBD(incidentDAO.getIncidentById(3));
//		assertEquals(3,incidentDAO.getNbIncidents());
//	}
	
	
	@Test
	void testMajEtatIncidentEnBDD() {
		incidentDAO.majEtatIncidentEnBD(incidentDAO.getIncidentById(1), 0);
		assertEquals(0,incidentDAO.getIncidentById(1).getEtat());
	}
	
// Ne sais pas comment le tester	
//	@Test
//	void testAssocierIncidentItineraire() {
//	}
	
	

}