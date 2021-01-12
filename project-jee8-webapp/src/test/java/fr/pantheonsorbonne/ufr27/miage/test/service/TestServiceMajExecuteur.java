package fr.pantheonsorbonne.ufr27.miage.test.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.LocalTime;
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

import fr.pantheonsorbonne.ufr27.miage.dao.ArretDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrainDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.VoyageDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.VoyageurDAO;
import fr.pantheonsorbonne.ufr27.miage.jms.MessageGateway;
import fr.pantheonsorbonne.ufr27.miage.jms.conf.JMSProducer;
import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.TrainRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.TrajetRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.VoyageRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.VoyageurRepository;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceMajDecideur;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceMajExecuteur;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceUtilisateur;
import fr.pantheonsorbonne.ufr27.miage.service.impl.ServiceMajDecideurImp;
import fr.pantheonsorbonne.ufr27.miage.service.impl.ServiceMajExecuteurImp;
import fr.pantheonsorbonne.ufr27.miage.service.impl.ServiceUtilisateurImp;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
public class TestServiceMajExecuteur {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator
			.from(ServiceMajDecideur.class, ServiceMajDecideurImp.class, ServiceMajExecuteur.class,
					ServiceMajExecuteurImp.class, ServiceUtilisateur.class, ServiceUtilisateurImp.class,
					TrainRepository.class, TrainDAO.class, ItineraireRepository.class, ItineraireDAO.class,
					TrajetRepository.class, TrajetDAO.class, ArretRepository.class, ArretDAO.class,
					VoyageurRepository.class, VoyageurDAO.class, VoyageRepository.class, VoyageDAO.class,
					MessageGateway.class, JMSProducer.class, TestPersistenceProducer.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	ServiceMajExecuteur serviceMajExecuteur;
	@Inject
	TrainRepository trainRepository;
	@Inject
	ArretRepository arretRepository;
	@Inject
	ItineraireRepository itineraireRepository;

	@BeforeAll
	void initVarInDB() {
		LocalDateTime now = LocalDateTime.now();
		Train train1 = new TrainAvecResa("Marque");

		Gare g1 = new Gare("Gare1");
		Gare g2 = new Gare("Gare2");
		Gare g3 = new Gare("Gare3");
		Gare g4 = new Gare("Gare4");

		Arret arret1 = new Arret(g1, null, now.plusMinutes(1));
		Arret arret2 = new Arret(g2, now.plusMinutes(2), now.plusMinutes(3));
		Arret arret3 = new Arret(g3, now.plusMinutes(4), now.plusMinutes(5));
		Arret arret4 = new Arret(g4, now.plusMinutes(6), null);
		List<Arret> arretsItineraire1 = new ArrayList<Arret>();
		arretsItineraire1.add(arret1);
		arretsItineraire1.add(arret2);
		arretsItineraire1.add(arret3);
		arretsItineraire1.add(arret4);

		Itineraire itineraire1 = new Itineraire();
		itineraire1.setTrain(train1);
		itineraire1.setEtat(CodeEtatItinieraire.EN_COURS.getCode());
		itineraire1.setArretsDesservis(arretsItineraire1);
		itineraire1.setArretActuel(arret1);

		em.getTransaction().begin();
		em.persist(train1);
		em.persist(g1);
		em.persist(g2);
		em.persist(g3);
		em.persist(g4);
		em.persist(arret1);
		em.persist(arret2);
		em.persist(arret3);
		em.persist(arret4);
		em.persist(itineraire1);
		em.getTransaction().commit();
	}

	@Test
	void testRetarderItineraire() {
		Train t = trainRepository.getTrainByBusinessId(1);
		Itineraire it = itineraireRepository.getItineraireByTrainEtEtat(t.getId(), CodeEtatItinieraire.EN_COURS);
		LocalDateTime heureArriveeDernierArret = it.getArretsDesservis().get(it.getArretsDesservis().size() - 1)
				.getHeureArriveeEnGare();
		LocalTime tmpsRetard = LocalTime.of(0, 0, 45);
		serviceMajExecuteur.retarderItineraire(it, tmpsRetard);
		assertEquals(heureArriveeDernierArret.plusSeconds(45),
				it.getArretsDesservis().get(it.getArretsDesservis().size() - 1).getHeureArriveeEnGare());
	}

}