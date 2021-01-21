package fr.pantheonsorbonne.ufr27.miage.test.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.GregorianCalendar;
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
		Gare g4 = new Gare("Gare4");
		Gare g5 = new Gare("Gare5");
		Gare g6 = new Gare("Gare6");

		Train t = new TrainAvecResa("Marque");
		Train t2 = new TrainAvecResa("TGV");
		Train t3 = new TrainAvecResa("OUIGO");

		Arret arret1 = new Arret(g1, null, HEURE_ACTUELLE.plusMinutes(1));
		Arret arret2 = new Arret(g2, HEURE_ACTUELLE.plusMinutes(2), HEURE_ACTUELLE.plusMinutes(3));
		Arret arret3 = new Arret(g3, HEURE_ACTUELLE.plusMinutes(4), null);
		Arret arret4 = new Arret(g1, null, HEURE_ACTUELLE.plusMinutes(3));
		Arret arret5 = new Arret(g3, HEURE_ACTUELLE.plusMinutes(5), null);
		Arret arret6 = new Arret(g2, null, HEURE_ACTUELLE.plusMinutes(3));
		Arret arret7 = new Arret(g4, HEURE_ACTUELLE.plusMinutes(4), HEURE_ACTUELLE.plusMinutes(5));
		Arret arret8 = new Arret(g5, HEURE_ACTUELLE.plusMinutes(6), null);
		Arret arret9 = new Arret(g3, null, HEURE_ACTUELLE.plusMinutes(3));
		Arret arret10 = new Arret(g5, HEURE_ACTUELLE.plusMinutes(4), HEURE_ACTUELLE.plusMinutes(5));
		Arret arret11 = new Arret(g6, HEURE_ACTUELLE.plusMinutes(6), null);

		Itineraire i1 = new Itineraire(t);
		i1.addArret(arret1);
		i1.addArret(arret2);
		i1.addArret(arret3);
		i1.setEtat(CodeEtatItinieraire.EN_COURS.getCode());
		Itineraire i2 = new Itineraire(t2);
		i2.addArret(arret4);
		i2.addArret(arret5);
		i2.setEtat(CodeEtatItinieraire.EN_COURS.getCode());
		Itineraire i3 = new Itineraire(t3);
		i3.addArret(arret6);
		i3.addArret(arret7);
		i3.addArret(arret8);
		i3.setEtat(CodeEtatItinieraire.EN_COURS.getCode());
		Itineraire i4 = new Itineraire(t);
		i4.addArret(arret9);
		i4.addArret(arret10);
		i4.addArret(arret11);
		i4.setEtat(CodeEtatItinieraire.EN_ATTENTE.getCode());

		em.getTransaction().begin();
		Gare[] gares = { g1, g2, g3, g4, g5, g6 };
		for (Gare gare : gares) {
			em.persist(gare);
		}
		Train[] trains = { t, t2, t3 };
		for (Train train : trains) {
			em.persist(train);
		}
		Arret[] arrets = { arret1, arret2, arret3, arret4, arret5, arret6, arret7, arret8, arret9, arret10, arret11 };
		for (Arret arret : arrets) {
			em.persist(arret);
		}
		Itineraire[] itineraires = { i1, i2, i3, i4 };
		for (Itineraire it : itineraires) {
			em.persist(it);
		}
		em.getTransaction().commit();

		BrokerUtils.startBroker();
	}

	@Test
	@Order(1)
	void testCreerIncident() throws DatatypeConfigurationException {
		Train t = trainRepository.getTrainByBusinessId(1);
		Train t2 = trainRepository.getTrainByBusinessId(2);
		Train t3 = trainRepository.getTrainByBusinessId(3);
		Itineraire i2 = itineraireRepository.getItineraireByTrainEtEtat(t2.getId(), CodeEtatItinieraire.EN_COURS);
		int nbArretsI2 = i2.getArretsDesservis().size();
		Itineraire i3 = itineraireRepository.getItineraireByTrainEtEtat(t3.getId(), CodeEtatItinieraire.EN_COURS);
		LocalDateTime oldEndI3 = i3.getArretsDesservis().get(i3.getArretsDesservis().size() - 1)
				.getHeureArriveeEnGare();
		Itineraire i4 = itineraireRepository.getItineraireByTrainEtEtat(t.getId(), CodeEtatItinieraire.EN_ATTENTE);
		LocalDateTime oldEndI4 = i4.getArretsDesservis().get(i4.getArretsDesservis().size() - 1)
				.getHeureArriveeEnGare();

		IncidentJAXB incidentJAXB = new IncidentJAXB();
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());
		XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		incidentJAXB.setDebutIncident(date2);
		incidentJAXB.setTypeIncident(CodeTypeIncident.ARBRE_SUR_VOIE.getCode());
		incidentJAXB.setEtatIncident(CodeEtatIncident.EN_COURS.getCode());

		assertTrue(serviceIncident.creerIncident(t.getId(), incidentJAXB));

		Incident incidentCree = incidentRepository.getIncidentByIdTrain(t.getId());
		int retardsEnSecondes = CodeTypeIncident.ARBRE_SUR_VOIE.getTempEstimation().toSecondOfDay();
		assertEquals(incidentCree.getHeureDebut().plusSeconds(retardsEnSecondes),
				incidentCree.getHeureTheoriqueDeFin());
		assertEquals(CodeTypeIncident.ARBRE_SUR_VOIE.getCode(), incidentCree.getTypeIncident());
		assertEquals(nbArretsI2 + 1, i2.getArretsDesservis().size());
		// L'itinéraire en correspondance n'est pas retardé car la condition sur les
		// voyageurs n'est pas remplie
		assertEquals(oldEndI3, i3.getArretsDesservis().get(i3.getArretsDesservis().size() - 1).getHeureArriveeEnGare());
		// En revanche le second itinéraire du train T1 est retardé
		assertEquals(oldEndI4.plusSeconds(retardsEnSecondes),
				i4.getArretsDesservis().get(i4.getArretsDesservis().size() - 1).getHeureArriveeEnGare());

	}

	@Test
	@Order(2)
	void testMajEtatIncident() {
		long ajoutDureeIncident = 5;
		ChronoUnit chronoUnitIncident = ChronoUnit.MINUTES;
		Train t = trainRepository.getTrainByBusinessId(1);
		Train t3 = trainRepository.getTrainByBusinessId(3);
		Itineraire i1 = itineraireRepository.getItineraireByTrainEtEtat(t.getId(), CodeEtatItinieraire.EN_INCIDENT);
		Itineraire i3 = itineraireRepository.getItineraireByTrainEtEtat(t3.getId(), CodeEtatItinieraire.EN_COURS);
		LocalDateTime oldEndI3 = i3.getArretsDesservis().get(i3.getArretsDesservis().size() - 1)
				.getHeureArriveeEnGare();
		Itineraire i4 = itineraireRepository.getItineraireByTrainEtEtat(t.getId(), CodeEtatItinieraire.EN_ATTENTE);
		LocalDateTime oldEndI4 = i4.getArretsDesservis().get(i4.getArretsDesservis().size() - 1)
				.getHeureArriveeEnGare();
		assertEquals(2, i1.getEtat());
		assertTrue(serviceIncident.majEtatIncident(t.getId(), CodeEtatIncident.EN_COURS.getCode(), ajoutDureeIncident,
				chronoUnitIncident));
		// Itinéraire = EN_COURS + Incident = EN_COURS --> on set l'etat à EN_INCIDENT
		assertEquals(2, i1.getEtat());
		// La condition des voyageurs n'est pas réalisée donc aucun retard n'est
		// appliqué à l'itinéraire ci-dessous
		assertEquals(oldEndI3, i3.getArretsDesservis().get(i3.getArretsDesservis().size() - 1).getHeureArriveeEnGare());
		// Le temps estimé de l'incident n'est pas encore dépassé donc les horaires des
		// arrêts des itinéraires impactés n'ont pas été modifiés lors du précédent
		// appel de majEtatIncident
		assertEquals(oldEndI4, i4.getArretsDesservis().get(i4.getArretsDesservis().size() - 1).getHeureArriveeEnGare());

		Incident incident = incidentRepository.getIncidentByIdTrain(t.getId());
		// Type 1 -- Animal sur voie
		assertEquals(CodeTypeIncident.ARBRE_SUR_VOIE.getCode(), incident.getTypeIncident());
		LocalDateTime heureDebutIncident = incident.getHeureDebut();
		LocalTime retardIncident = CodeTypeIncident.ARBRE_SUR_VOIE.getTempEstimation();
		LocalDateTime heureCalculeeDeFin = heureDebutIncident.plusSeconds(retardIncident.toSecondOfDay());
		assertEquals(heureCalculeeDeFin, incident.getHeureTheoriqueDeFin());

		ajoutDureeIncident = 10;
		LocalDateTime heureTheoriqueDeFin = LocalDateTime.now().minusSeconds(1);
		incident.setHeureTheoriqueDeFin(heureTheoriqueDeFin);
		assertTrue(serviceIncident.majEtatIncident(t.getId(), CodeEtatIncident.EN_COURS.getCode(), ajoutDureeIncident,
				chronoUnitIncident));
		assertEquals(heureTheoriqueDeFin.plus(ajoutDureeIncident, chronoUnitIncident),
				incident.getHeureTheoriqueDeFin());
		assertEquals(2, i1.getEtat());
		assertEquals(oldEndI3, i3.getArretsDesservis().get(i3.getArretsDesservis().size() - 1).getHeureArriveeEnGare());
		assertEquals(oldEndI4.plus(ajoutDureeIncident, chronoUnitIncident),
				i4.getArretsDesservis().get(i4.getArretsDesservis().size() - 1).getHeureArriveeEnGare());
		assertTrue(serviceIncident.majEtatIncident(t.getId(), CodeEtatIncident.RESOLU.getCode(), ajoutDureeIncident,
				chronoUnitIncident));
		assertEquals(1, i1.getEtat());
		assertEquals(oldEndI3, i3.getArretsDesservis().get(i3.getArretsDesservis().size() - 1).getHeureArriveeEnGare());
		assertTrue(oldEndI4.plus(ajoutDureeIncident, chronoUnitIncident)
				.isAfter(i4.getArretsDesservis().get(i4.getArretsDesservis().size() - 1).getHeureArriveeEnGare()));
	}

	@AfterAll
	void nettoyageDonnees() {
		testDatabase.clear();
		BrokerUtils.stopBroker();
	}

}