package fr.pantheonsorbonne.ufr27.miage.test.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import fr.pantheonsorbonne.ufr27.miage.dao.ArretDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.GareDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.IncidentDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrainDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.VoyageDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.VoyageurDAO;
import fr.pantheonsorbonne.ufr27.miage.jms.MessageGateway;
import fr.pantheonsorbonne.ufr27.miage.jms.conf.JMSProducer;
import fr.pantheonsorbonne.ufr27.miage.jms.utils.BrokerUtils;
import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Incident;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.jpa.Incident.CodeEtatIncident;
import fr.pantheonsorbonne.ufr27.miage.jpa.Incident.CodeTypeIncident;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.IncidentJAXB;
import fr.pantheonsorbonne.ufr27.miage.repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.IncidentRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.TrainRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.TrajetRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.VoyageRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.VoyageurRepository;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceIncident;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceMajDecideur;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceMajExecuteur;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceMajInfoGare;
import fr.pantheonsorbonne.ufr27.miage.service.impl.ServiceIncidentImp;
import fr.pantheonsorbonne.ufr27.miage.service.impl.ServiceMajDecideurImp;
import fr.pantheonsorbonne.ufr27.miage.service.impl.ServiceMajExecuteurImp;
import fr.pantheonsorbonne.ufr27.miage.service.impl.ServiceMajInfoGareImp;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestDatabase;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
@TestMethodOrder(OrderAnnotation.class)
public class TestServiceIncident {

	private final static LocalDateTime HEURE_ACTUELLE = LocalDateTime.now();

	@WeldSetup
	private WeldInitiator weld = WeldInitiator.from(ServiceIncident.class, ServiceIncidentImp.class,
			ServiceMajDecideur.class, ServiceMajDecideurImp.class, ServiceMajExecuteur.class,
			ServiceMajExecuteurImp.class, ServiceMajInfoGare.class, ServiceMajInfoGareImp.class, TrainRepository.class,
			IncidentRepository.class, ItineraireRepository.class, ArretRepository.class, VoyageurRepository.class,
			VoyageRepository.class, TrajetRepository.class, VoyageurDAO.class, VoyageDAO.class, TrajetDAO.class,
			ItineraireDAO.class, IncidentDAO.class, ArretDAO.class, TrainDAO.class, GareDAO.class, MessageGateway.class,
			JMSProducer.class, TestPersistenceProducer.class, TestDatabase.class).activate(RequestScoped.class).build();

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
	@Inject
	TestDatabase testDatabase;

	@BeforeAll
	void initVarInDB() {
		Gare g1 = new Gare("Gare1");
		Gare g2 = new Gare("Gare2");
		Gare g3 = new Gare("Gare3");

		Train t = new TrainAvecResa("Marque");
		Train t2 = new TrainAvecResa("TGV");
		Itineraire i1 = new Itineraire(t);
		i1.setEtat(CodeEtatItinieraire.EN_COURS.getCode());

		Itineraire i2 = new Itineraire(t2);
		i2.setEtat(CodeEtatItinieraire.EN_ATTENTE.getCode());
		Arret arret1 = new Arret(g1, null, HEURE_ACTUELLE.plusMinutes(1));
		Arret arret2 = new Arret(g2, HEURE_ACTUELLE.plusMinutes(2), HEURE_ACTUELLE.plusMinutes(3));
		Arret arret3 = new Arret(g3, HEURE_ACTUELLE.plusMinutes(4), null);
		List<Arret> arretsI2 = new ArrayList<Arret>();
		arretsI2.add(arret1);
		arretsI2.add(arret2);
		arretsI2.add(arret3);
		i1.setArretsDesservis(arretsI2);
		i2.setArretsDesservis(arretsI2);

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

		BrokerUtils.startBroker();
	}

	@Test
	@Order(1)
	void testCreerIncident() throws DatatypeConfigurationException {
		Train t = trainRepository.getTrainByBusinessId(1);
		IncidentJAXB incidentJAXB = new IncidentJAXB();

		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());
		XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		incidentJAXB.setDebutIncident(date2);
		incidentJAXB.setTypeIncident(1);
		incidentJAXB.setEtatIncident(CodeEtatIncident.EN_COURS.getCode());

		assertTrue(serviceIncident.creerIncident(t.getId(), incidentJAXB));

		Incident incidentCree = incidentRepository.getIncidentByIdTrain(t.getId());
		assertEquals(incidentCree.getHeureDebut().plusMinutes(5), incidentCree.getHeureTheoriqueDeFin());
		assertEquals(CodeTypeIncident.ANIMAL_SUR_VOIE.getCode(), incidentCree.getTypeIncident());
	}

	@Test
	@Order(2)
	void testMajEtatIncident() {
		long ajoutDureeIncident = 5;
		ChronoUnit chronoUnitIncident = ChronoUnit.MINUTES;
		Train t = trainRepository.getTrainByBusinessId(1);
		Itineraire itineraire = itineraireRepository.getItineraireByTrainEtEtat(t.getId(),
				CodeEtatItinieraire.EN_INCIDENT);
		assertEquals(2, itineraire.getEtat());
		assertTrue(serviceIncident.majEtatIncident(t.getId(), CodeEtatIncident.EN_COURS.getCode(), ajoutDureeIncident,
				chronoUnitIncident));
		assertNotNull(itineraire);
		// Itinéraire = EN_COURS + Incident = EN_COURS --> on set l'etat à EN_INCIDENT
		assertEquals(2, itineraire.getEtat());
		Incident incident = incidentRepository.getIncidentByIdTrain(t.getId());
		// Type 1 -- Animal sur voie
		assertEquals(CodeTypeIncident.ANIMAL_SUR_VOIE.getCode(), incident.getTypeIncident());
		LocalDateTime heureDebutIncident = incident.getHeureDebut();
		LocalTime retardIncident = CodeTypeIncident.getTempEstimation(CodeTypeIncident.ANIMAL_SUR_VOIE.getCode());
		LocalDateTime heureCalculeeDeFin = heureDebutIncident.plusSeconds(retardIncident.toSecondOfDay());
		assertEquals(heureCalculeeDeFin, incident.getHeureTheoriqueDeFin());

		ajoutDureeIncident = 10;
		LocalDateTime heureTheoriqueDeFin = LocalDateTime.now().minusSeconds(1);
		incident.setHeureTheoriqueDeFin(heureTheoriqueDeFin);
		System.out.println(incident.getHeureTheoriqueDeFin());
		assertTrue(serviceIncident.majEtatIncident(t.getId(), CodeEtatIncident.EN_COURS.getCode(), ajoutDureeIncident,
				chronoUnitIncident));
		System.out.println(incident.getHeureTheoriqueDeFin());
		assertEquals(heureTheoriqueDeFin.plus(ajoutDureeIncident, chronoUnitIncident),
				incident.getHeureTheoriqueDeFin());

		assertEquals(2, itineraire.getEtat());
		assertTrue(serviceIncident.majEtatIncident(t.getId(), CodeEtatIncident.RESOLU.getCode(), ajoutDureeIncident,
				chronoUnitIncident));
		assertEquals(1, itineraire.getEtat());

		// TODO : Finir ce cas de test

		// 1. Voir l'impact du rallongement de l'incident sur it2
		// 2. Voir l'impact de la terminaison de l'incident avant les 5min de
		// rallongement sur it2

		// NB : it2 est après itinéraire (peut-être init des données à revoir dans le
		// BeforeAll)

		// ==> Voir comment tester la dernière partie de la méthode
	}

	@AfterAll
	void nettoyageDonnees() {
		testDatabase.clear();
		BrokerUtils.stopBroker();
	}

}