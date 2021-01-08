package fr.pantheonsorbonne.ufr27.miage.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

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

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.IncidentJAXB;
import fr.pantheonsorbonne.ufr27.miage.n_dao.ArretDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.IncidentDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.TrainDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.VoyageDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.VoyageurDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jms.MessageGateway;
import fr.pantheonsorbonne.ufr27.miage.n_jms.conf.JMSProducer;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident.CodeEtatIncident;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident.CodeTypeIncident;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.IncidentRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.TrainRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.TrajetRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.VoyageRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.VoyageurRepository;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceIncident;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceMajDecideur;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceMajExecuteur;
import fr.pantheonsorbonne.ufr27.miage.n_service.impl.ServiceIncidentImp;
import fr.pantheonsorbonne.ufr27.miage.n_service.impl.ServiceMajDecideurImp;
import fr.pantheonsorbonne.ufr27.miage.n_service.impl.ServiceMajExecuteurImp;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
@TestMethodOrder(OrderAnnotation.class)
public class TestServiceIncident {

	private final static LocalDateTime HEURE_ACTUELLE = LocalDateTime.now();

	@WeldSetup
	private WeldInitiator weld = WeldInitiator
			.from(ServiceIncident.class, ServiceIncidentImp.class, TrainRepository.class, TrainDAO.class,
					IncidentRepository.class, IncidentDAO.class, ItineraireRepository.class, ItineraireDAO.class,
					TrajetRepository.class, TrajetDAO.class, ArretRepository.class, ArretDAO.class,
					ServiceMajDecideur.class, ServiceMajDecideurImp.class, ServiceMajExecuteur.class,
					ServiceMajExecuteurImp.class, VoyageurRepository.class, VoyageurDAO.class, VoyageRepository.class,
					VoyageDAO.class, MessageGateway.class, JMSProducer.class, TestPersistenceProducer.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	ServiceIncident serviceIncident;
	@Inject
	TrainRepository trainRepository;
	@Inject
	IncidentRepository incidentRepository;
	@Inject
	ItineraireRepository itineraireRepository;

	@BeforeAll
	void initVarInDB() {
		Gare g1 = new Gare("Gare1");
		Gare g2 = new Gare("Gare2");
		Gare g3 = new Gare("Gare3");

		Train t = new TrainAvecResa(1, "Marque");
		Train t2 = new TrainAvecResa(2, "TGV");
		Itineraire i1 = new Itineraire(t);
		i1.setEtat(CodeEtatItinieraire.EN_COURS.getCode());

		Itineraire i2 = new Itineraire(t2);
		i2.setEtat(CodeEtatItinieraire.EN_ATTENTE.getCode());
		Arret arret1 = new Arret(g1, null, HEURE_ACTUELLE.plus(1, ChronoUnit.MINUTES));
		Arret arret2 = new Arret(g2, HEURE_ACTUELLE.plus(2, ChronoUnit.MINUTES),
				HEURE_ACTUELLE.plus(3, ChronoUnit.MINUTES));
		Arret arret3 = new Arret(g3, HEURE_ACTUELLE.plus(4, ChronoUnit.MINUTES), null);
		List<Arret> arretsI2 = new ArrayList<Arret>();
		arretsI2.add(arret1);
		arretsI2.add(arret2);
		arretsI2.add(arret3);
		i1.setArretsDesservis(arretsI2);
		i2.setArretsDesservis(arretsI2);
		i2.setArretActuel(arret1);

		em.getTransaction().begin();
		em.persist(g1);
		em.persist(g2);
		em.persist(g3);
		em.persist(t);
		em.persist(t2);
		em.persist(arret1);
		em.persist(arret2);
		em.persist(arret3);
		em.persist(i1);
		em.persist(i2);
		em.getTransaction().commit();
	}

	@Test
	@Order(1)
	void testCreerIncident() throws DatatypeConfigurationException {
		Train t = this.trainRepository.getTrainById(1);
		IncidentJAXB incidentJAXB = new IncidentJAXB();

		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());
		XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		incidentJAXB.setHeureIncident(date2);
		incidentJAXB.setTypeIncident(1);
		incidentJAXB.setEtatIncident(CodeEtatIncident.EN_COURS.getCode());

		assertEquals(0, this.incidentRepository.getNbIncidents());
		this.serviceIncident.creerIncident(t.getId(), incidentJAXB);
		assertEquals(1, this.incidentRepository.getNbIncidents());

		Incident incidentCree = this.incidentRepository.getIncidentByIdTrain(t.getId());
		assertEquals(incidentCree.getHeureDebut().plusMinutes(5), incidentCree.getHeureTheoriqueDeFin());
		assertEquals(5, incidentCree.getDuree());
	}

	@Test
	@Order(2)
	void testMajEtatIncident() {
		long ajoutDuree = 3;
		Train t = this.trainRepository.getTrainById(1);
		Itineraire itineraire = this.itineraireRepository.getItineraireByTrainEtEtat(t.getId(),
				CodeEtatItinieraire.EN_COURS);
		assertEquals(1, itineraire.getEtat());
		assertEquals(true, this.serviceIncident.majEtatIncident(t.getId(), CodeEtatIncident.EN_COURS.getCode(),
				ajoutDuree, ChronoUnit.MINUTES));
		assertNotNull(itineraire);
		// Itineraire = EN_COURS + Incident = EN_COURS --> on set l'etat à EN_INCIDENT
		assertEquals(2, itineraire.getEtat());
		Incident incident = this.incidentRepository.getIncidentByIdTrain(t.getId());
		System.out.println(incident.getTypeIncident()); // Type 1 -- Animal sur voie -- 5 minutes
		assertEquals(5, incident.getDuree());
		assertEquals(incident.getHeureDebut().plusMinutes(5), incident.getHeureTheoriqueDeFin());

		ajoutDuree = 10;
		long incidentDureeInit = incident.getDuree();
		LocalDateTime heureTheoriqueInit = incident.getHeureTheoriqueDeFin();
		assertEquals(true, this.serviceIncident.majEtatIncident(t.getId(), CodeEtatIncident.EN_COURS.getCode(),
				ajoutDuree, ChronoUnit.MINUTES));
		assertEquals(incidentDureeInit + ajoutDuree, incident.getDuree());
		assertEquals(heureTheoriqueInit.plusMinutes(ajoutDuree), incident.getHeureTheoriqueDeFin());

		assertEquals(2, itineraire.getEtat());
		assertEquals(true, this.serviceIncident.majEtatIncident(t.getId(), CodeEtatIncident.RESOLU.getCode(),
				ajoutDuree, ChronoUnit.MINUTES));
		assertEquals(1, itineraire.getEtat());

		// TODO : Finir ce cas de test

		// 1. Voir l'impact du rallongement de l'incident sur it2
		// 2. Voir l'impact de la terminaison de l'incident avant les 5min de
		// rallongement sur it2

		// NB : it2 est après itineraire (ptetre init des données à revoir dans le
		// BeforeAll)

		// ==> Voir comment tester la dernière partie de la méthode
	}

}