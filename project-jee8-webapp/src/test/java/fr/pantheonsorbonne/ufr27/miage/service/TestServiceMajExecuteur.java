package fr.pantheonsorbonne.ufr27.miage.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.LocalTime;
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

import fr.pantheonsorbonne.ufr27.miage.n_dao.ArretDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.TrainDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.TrainRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.TrajetRepository;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceMajExecuteur;
import fr.pantheonsorbonne.ufr27.miage.n_service.impl.ServiceMajExecuteurImp;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
public class TestServiceMajExecuteur {

	private final static LocalDateTime HEURE_ACTUELLE = LocalDateTime.now();

	@WeldSetup
	private WeldInitiator weld = WeldInitiator.from(ServiceMajExecuteur.class, ServiceMajExecuteurImp.class, 
			ItineraireRepository.class, ItineraireDAO.class, 
			ArretRepository.class, ArretDAO.class, TrainRepository.class, TrainDAO.class,
			TrajetRepository.class, TrajetDAO.class, TestPersistenceProducer.class)
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
		Train train = new TrainAvecResa(1, "Marque");
		
		Gare g1 = new Gare("Gare1");
		Gare g2 = new Gare("Gare2");
		Gare g3 = new Gare("Gare3");
		
		Itineraire it = new Itineraire(train);
		it.setEtat(CodeEtatItinieraire.EN_COURS.getCode());
		Arret arret1 = new Arret(g1, null, HEURE_ACTUELLE.plus(1, ChronoUnit.MINUTES));
		Arret arret2 = new Arret(g2, HEURE_ACTUELLE.plus(2, ChronoUnit.MINUTES), HEURE_ACTUELLE.plus(3, ChronoUnit.MINUTES));
		Arret arret3 = new Arret(g3, HEURE_ACTUELLE.plus(4, ChronoUnit.MINUTES), null);
		List<Arret> arretsI2 = new ArrayList<Arret>();
		arretsI2.add(arret1); arretsI2.add(arret2); arretsI2.add(arret3);
		it.setArretsDesservis(arretsI2);
		it.setArretActuel(arret1);
		
		em.getTransaction().begin();
		em.persist(train);
		em.persist(g1);
		em.persist(g2);
		em.persist(g3);
		em.persist(arret1);
		em.persist(arret2);
		em.persist(arret3);
		em.persist(it);
		em.getTransaction().commit();
	}
	
	@Test
	void testSupprArret() {
		Train t = this.trainRepository.getTrainById(1);
		Itineraire it = this.itineraireRepository.getItineraireByTrainEtEtat(t.getId(), CodeEtatItinieraire.EN_COURS);
		Arret arretToSuppr = this.arretRepository.getArretParItineraireEtNomGare(it, "Gare2");
		int nbArrets = it.getArretsDesservis().size();// 3
		this.serviceMajExecuteur.supprimerArret(t.getId(), arretToSuppr);
		assertEquals(nbArrets-1, it.getArretsDesservis().size());
	}
	
	@Test
	void testRetarderTrain() {
		Train t = this.trainRepository.getTrainById(1);
		Itineraire it = this.itineraireRepository.getItineraireByTrainEtEtat(t.getId(), CodeEtatItinieraire.EN_COURS);
		LocalDateTime heureArriveeDernierArret = it.getArretsDesservis().get(it.getArretsDesservis().size()-1).getHeureArriveeEnGare();
		LocalTime tmpsRetard = LocalTime.of(0, 0, 45);
		this.serviceMajExecuteur.retarderTrain(t.getId(), tmpsRetard);
		assertEquals(heureArriveeDernierArret.plusSeconds(45), it.getArretsDesservis().get(it.getArretsDesservis().size()-1).getHeureArriveeEnGare());
	}

}