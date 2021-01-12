package fr.pantheonsorbonne.ufr27.miage.test.repository;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import fr.pantheonsorbonne.ufr27.miage.dao.ArretDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
public class TestArretRepository {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator.from(ArretRepository.class, ArretDAO.class, TestPersistenceProducer.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	ArretRepository arretRepository;

	@Test
	void testGetArretParItineraireEtNomGare() {
		Arret arret1 = new Arret(new Gare("Gare1"), LocalDateTime.now(), LocalDateTime.now().plus(1, ChronoUnit.MINUTES));
		Arret arret2 = new Arret(new Gare("Gare2"), LocalDateTime.now().plus(2, ChronoUnit.MINUTES), LocalDateTime.now().plus(3, ChronoUnit.MINUTES));
		Arret arret3 = new Arret(new Gare("Gare3"), LocalDateTime.now().plus(4, ChronoUnit.MINUTES), LocalDateTime.now().plus(5, ChronoUnit.MINUTES));
		List<Arret> arretsItineraire1 = new ArrayList<Arret>();
		arretsItineraire1.add(arret1); arretsItineraire1.add(arret2); arretsItineraire1.add(arret3);
		
		Itineraire i = new Itineraire();
		i.setEtat(0);
		i.setArretsDesservis(arretsItineraire1);
		
		assertEquals(arret2, this.arretRepository.getArretParItineraireEtNomGare(i, "Gare2"));
		assertEquals(null, this.arretRepository.getArretParItineraireEtNomGare(i, "Gare4"));
	}

}