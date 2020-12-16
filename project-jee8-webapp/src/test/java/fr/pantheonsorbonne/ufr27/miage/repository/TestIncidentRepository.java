package fr.pantheonsorbonne.ufr27.miage.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import fr.pantheonsorbonne.ufr27.miage.n_dao.ArretDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.IncidentDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.TrainDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident.CodeEtatIncident;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident.CodeTypeIncident;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.IncidentRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.TrainRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.TrajetRepository;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
@TestMethodOrder(OrderAnnotation.class)
public class TestIncidentRepository {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator.from(IncidentRepository.class, IncidentDAO.class,
			 ItineraireRepository.class, ItineraireDAO.class, TrainRepository.class,
			 TrainDAO.class, TrajetRepository.class, TrajetDAO.class, 
			 ArretRepository.class, ArretDAO.class, TestPersistenceProducer.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	
	@Inject
	TrainRepository trainRepository;
	@Inject
	ItineraireRepository itineraireRepository;
	@Inject
	IncidentRepository incidentRepository;


	@BeforeAll
	void initVarInDB() {
		Train train1 = new TrainAvecResa(1, "Marque");
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
		Train t = this.trainRepository.getTrainById(1);
		assertNotNull(t);
		Incident i = new Incident();
		i.setTypeIncident(CodeTypeIncident.ANIMAL_SUR_VOIE.getCode());
		i.setEtat(CodeEtatIncident.EN_COURS.getCode());
		i.setDuree(30);
		assertNotNull(i);
		assertTrue(this.incidentRepository.creerIncident(t.getId(), i));
		Itineraire itAssocieT = this.itineraireRepository.getItineraireByTrainEtEtat(t.getId(), CodeEtatItinieraire.EN_COURS);
		assertEquals(itAssocieT.getIncident(), i);
		
		em.getTransaction().begin();
		em.persist(i);
		itAssocieT.setIncident(i);
		em.getTransaction().commit();
		assertEquals(CodeTypeIncident.ANIMAL_SUR_VOIE.getCode(), itAssocieT.getIncident().getTypeIncident());
	}
	
	@Test
	@Order(2)
	void testGetIncidentByIdTrain() {
		Train t = this.trainRepository.getTrainById(1);
		assertEquals(30, this.incidentRepository.getIncidentByIdTrain(t.getId()).getDuree());
	}
	
	@Test
	@Order(3)
	void testUpdateEtatIncident() {
		Train t = this.trainRepository.getTrainById(1);		
		this.incidentRepository.updateEtatIncident(t.getId(), CodeEtatIncident.RESOLU.getCode());
		assertEquals(CodeEtatIncident.RESOLU.getCode(),  this.incidentRepository.getIncidentByIdTrain(t.getId()).getEtat());
	}
	
	

}