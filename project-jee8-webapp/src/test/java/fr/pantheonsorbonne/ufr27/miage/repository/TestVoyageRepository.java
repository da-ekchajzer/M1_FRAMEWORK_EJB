package fr.pantheonsorbonne.ufr27.miage.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import fr.pantheonsorbonne.ufr27.miage.n_dao.VoyageDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyage;
import fr.pantheonsorbonne.ufr27.miage.n_repository.VoyageRepository;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
public class TestVoyageRepository {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator.from(VoyageRepository.class, VoyageDAO.class,
			TestPersistenceProducer.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	VoyageRepository voyageRepository;
	
	@Test
	void testGetVoyagesComposedByAtLeastOneTrajetOf() {
		Itineraire i1 = new Itineraire();
		Itineraire i2 = new Itineraire();
		Itineraire i3 = new Itineraire();
		Itineraire i4 = new Itineraire();
		
		Gare g1 = new Gare("Gare1");
		Gare g2 = new Gare("Gare2");
		Gare g3 = new Gare("Gare3");
		Gare g4 = new Gare("Gare4");
		Gare g5 = new Gare("Gare5");
		Gare g6 = new Gare("Gare6");
		
		Trajet trajet1 = new Trajet(g1, g2, i1, 1);
		Trajet trajet2 = new Trajet(g1, g3, i1, 1);
		Trajet trajet3 = new Trajet(g3, g4, i1, 1);
		Trajet trajet4 = new Trajet(g5, g6, i2, 1);
		Trajet trajet5 = new Trajet(g6, g2, i2, 1);
		List<Trajet> trajetsV1 = new ArrayList<Trajet>();
		trajetsV1.add(trajet1); trajetsV1.add(trajet2); trajetsV1.add(trajet3);
		List<Trajet> trajetsV2 = new ArrayList<Trajet>();
		trajetsV2.add(trajet1); trajetsV2.add(trajet4); trajetsV2.add(trajet5);
		Voyage voyage1 = new Voyage(trajetsV1);
		Voyage voyage2 = new Voyage(trajetsV2);

		em.getTransaction().begin();
		em.persist(g1);
		em.persist(g2);
		em.persist(g3);
		em.persist(g4);
		em.persist(g5);
		em.persist(g6);
		
		em.persist(i1);
		em.persist(i2);
		em.persist(i3);
		em.persist(i4);

		em.persist(trajet1);
		em.persist(trajet2);
		em.persist(trajet3);
		em.persist(trajet4);
		em.persist(trajet5);
		
		em.persist(voyage1);
		em.persist(voyage2);
		em.getTransaction().commit();
		
		List<Trajet> trajets = new ArrayList<Trajet>();
		trajets.add(trajet2); trajets.add(trajet3);
		// TODO : cf fonction car retourne 2 fois le même voyage (Set pas ok car Voyable n'implémente pas Comparable)
		List<Voyage> v = this.voyageRepository.getVoyagesComposedByAtLeastOneTrajetOf(trajets);
		assertEquals(1, v.size());
		trajets.add(trajet1);
		assertEquals(2, this.voyageRepository.getVoyagesComposedByAtLeastOneTrajetOf(trajets).size());
	}

}