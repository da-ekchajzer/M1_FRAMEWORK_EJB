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
import fr.pantheonsorbonne.ufr27.miage.n_dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.n_repository.IncidentRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.TrainRepository;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceItineraire;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
public class TestServiceItineraire {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator.from(ItineraireDAO.class, TestPersistenceProducer.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	ServiceItineraire serviceItineraire;
	
	@Inject
	TrainRepository trainRepository;


	@BeforeAll
	void initVarInDB() {
		Train train1 = new TrainAvecResa(1, "Marque");
		
		Arret arret1 = new Arret(new Gare("Gare1"), LocalDateTime.now(), LocalDateTime.now().plus(1, ChronoUnit.MINUTES));
		Arret arret2 = new Arret(new Gare("Gare2"), LocalDateTime.now().plus(2, ChronoUnit.MINUTES), LocalDateTime.now().plus(3, ChronoUnit.MINUTES));
		Arret arret3 = new Arret(new Gare("Gare3"), LocalDateTime.now().plus(4, ChronoUnit.MINUTES), LocalDateTime.now().plus(5, ChronoUnit.MINUTES));
		List<Arret> arretsItineraire1 = new ArrayList<Arret>();
		arretsItineraire1.add(arret1); arretsItineraire1.add(arret2); arretsItineraire1.add(arret3);
		
		Itineraire itineraire1 = new Itineraire();
		itineraire1.setTrain(train1);
		itineraire1.setEtat(0);
		itineraire1.setArretsDesservis(arretsItineraire1);
		
		em.getTransaction().begin();
		em.persist(train1);
		em.persist(itineraire1);
		em.getTransaction().commit();
	}
	
	@Test
	void testGetItineraireJaxbByIdTrain() {
		Train t = this.trainRepository.getTrainById(1);
		ItineraireJAXB itineraireJAXB = this.serviceItineraire.getItineraire(t.getId());
		assertEquals(itineraireJAXB.getEtatItineraire(), 0);
		assertEquals(itineraireJAXB.getArrets().get(0).getGare(), "Gare1");
	}
	
	@Test
	void testMajItineraire() {
		Train t = this.trainRepository.getTrainById(1);
		ArretJAXB arretJAXB = new ArretJAXB();
		arretJAXB.setGare("Gare2");
		assertEquals(true,  this.serviceItineraire.majItineraire(t.getId(),  arretJAXB));
	}

}