package fr.pantheonsorbonne.ufr27.miage.dao;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
import fr.pantheonsorbonne.ufr27.miage.n_dao.VoyageDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.VoyageurDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainSansResa;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyage;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyageur;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.TrajetRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.VoyageRepository;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
public class TestVoyageurDAO {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator.from(VoyageurDAO.class, ItineraireRepository.class,
			TrajetRepository.class, ItineraireDAO.class, VoyageRepository.class, TrajetDAO.class, TrainDAO.class,
			VoyageDAO.class, ArretRepository.class, ArretDAO.class, TrajetDAO.class, TestPersistenceProducer.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	VoyageurDAO voyageurDAO;
	@Inject
	TrainDAO trainDAO;
	@Inject
	TrajetDAO trajetDAO;
	@Inject
	ItineraireDAO itineraireDAO;

	@BeforeAll
	public void setup() {
		em.getTransaction().begin();

		// --------------------------------- Gares
		String[] nomGares = { "Paris - Gare de Lyon", "Avignon-Centre", "Aix en Provence", "Marseille - St Charles",
				"Dijon-Ville", "Lyon - Pardieu", "Narbonne", "Sete", "Perpignan", "Paris - Montparnasse", "Tours",
				"Bordeaux - Saint-Jean", "Pessac", "Arcachon-Centre", "Nantes" };

		Map<String, Gare> gares = new HashMap<>();
		for (String nomGare : nomGares) {
			Gare g = new Gare(nomGare);
			gares.put(nomGare, g);
			em.persist(g);
		}

		// --------------------------------- Remplissage de la table Train
		Train train1 = new TrainAvecResa(1, "TGV");
		Train train2 = new TrainSansResa(2, "TER");
		Train train3 = new TrainAvecResa(3, "OUIGO");
		Train train4 = new TrainAvecResa(4, "OUIGO");
		Train train5 = new TrainSansResa(5, "TER");
		Train train6 = new TrainAvecResa(6, "TGV");
		Train train7 = new TrainSansResa(7, "TER");
		Train train8 = new TrainAvecResa(8, "TGV");
		Train train9 = new TrainAvecResa(9, "TGV");

		Train[] trains = { train1, train2, train3, train4, train5, train6, train7, train8, train9 };
		for (Train t : trains)
			em.persist(t);

		// --------------------------------- Arrêts

		Arret arret1 = new Arret(gares.get("Paris - Gare de Lyon"), null, LocalDateTime.now());
		Arret arret2 = new Arret(gares.get("Avignon-Centre"), LocalDateTime.now().plus(1, ChronoUnit.MINUTES),
				LocalDateTime.now().plus(1, ChronoUnit.MINUTES).plus(30, ChronoUnit.SECONDS));
		Arret arret3 = new Arret(gares.get("Aix en Provence"), LocalDateTime.now().plus(3, ChronoUnit.MINUTES),
				LocalDateTime.now().plus(3, ChronoUnit.MINUTES).plus(1, ChronoUnit.MINUTES));
		Arret arret4 = new Arret(gares.get("Marseille - St Charles"), LocalDateTime.now().plus(5, ChronoUnit.MINUTES),
				null);

		Arret[] arrets = { arret1, arret2, arret3, arret4 };

		for (Arret a : arrets)
			em.persist(a);

		// --------------------------------- Remplissage de la table Itinéraire

		Itineraire itineraire1 = new Itineraire(train1);
		itineraire1.addArret(arret1);
		itineraire1.setArretActuel(arret1);
		itineraire1.addArret(arret2);
		itineraire1.addArret(arret3);
		itineraire1.addArret(arret4);

		em.persist(itineraire1);

		// --------------------------------- Remplissage de la table Trajet

		Trajet trajet1 = new Trajet(gares.get("Paris - Gare de Lyon"), gares.get("Avignon-Centre"), itineraire1, 0);
		Trajet trajet2 = new Trajet(gares.get("Avignon-Centre"), gares.get("Aix en Provence"), itineraire1, 1);
		Trajet trajet3 = new Trajet(gares.get("Aix en Provence"), gares.get("Marseille - St Charles"), itineraire1, 2);

		Trajet[] trajets = { trajet1, trajet2, trajet3 };

		for (Trajet t : trajets)
			em.persist(t);

		// --------------------------------- Remplissage de la table Voyage
		List<Trajet> voyageTrajet1 = new LinkedList<Trajet>();
		voyageTrajet1.add(trajet1);
		voyageTrajet1.add(trajet2);
		Voyage voyage1 = new Voyage(voyageTrajet1);

		List<Trajet> voyageTrajet2 = new LinkedList<Trajet>();
		voyageTrajet2.add(trajet2);
		voyageTrajet2.add(trajet3);
		Voyage voyage2 = new Voyage(voyageTrajet2);

		em.persist(voyage1);
		em.persist(voyage2);

		// --------------------------------- Remplissage de la table Voyageur

		String[] prenomsVoyageurs = { "Mariah", "Marc", "Sophia", "Alyssia", "Antoine", "Doudouh", "Lucie", "Lucas",
				"David", "Ben", "Maria", "Lucas", "Sophie", "Jean-Mi", "Jean", "Abdel", "Tatiana", "Charlotte",
				"Charlotte", "Abdel", "Ben", "Ben", "Mathieu", "Louis", "Jean-Luc", "Luc", "Jean", "Sophia", "Marc",
				"Manuel", "Abdel" };

		String[] nomsVoyageurs = { "Dupont", "Dupont", "Durand", "Martin", "Bernard", "Thomas", "Petit", "Grand",
				"Robert", "Richard", "Richard", "Dubois", "Petit", "Petit", "Moreau", "Laurent", "Simon", "Michel",
				"Lefevre", "Legrand", "Lefebvre", "Leroy", "Roux", "Leroi", "Morel", "Fournier", "Gerard", "Poirier",
				"Pommier", "Rossignol", "Benamara" };

		for (int i = 0; i < prenomsVoyageurs.length; i++) {
			Voyageur v = new Voyageur(prenomsVoyageurs[i], nomsVoyageurs[i]);
			if (i < 5)
				voyage1.addVoyageur(v);
			if (i >= 5 && i < 8)
				voyage2.addVoyageur(v);
			em.persist(v);
		}

		em.getTransaction().commit();
	}

	@Test
	void testGetVoyageursByVoyage() {
		Voyageur voyageur1 = new Voyageur();
		Voyageur voyageur2 = new Voyageur();
		Voyage voyage1 = new Voyage();
		em.getTransaction().begin();
		em.persist(voyageur1);
		em.persist(voyageur2);
		em.persist(voyage1);
		voyageur1.setVoyage(voyage1);
		voyageur2.setVoyage(voyage1);
		em.getTransaction().commit();
		List<Voyageur> voyageurs = voyageurDAO.getVoyageursByVoyage(voyage1);
		assertEquals(2, voyageurs.size());

	}

	@Test
	void testMettreVoyageursDansItineraire() {
		Itineraire itineraire1 = new Itineraire();
		Voyageur voyageur1 = new Voyageur();
		Voyageur voyageur2 = new Voyageur();
		em.getTransaction().begin();
		em.persist(voyageur1);
		em.persist(voyageur2);
		em.persist(itineraire1);
		List<Voyageur> voyageurs = new ArrayList<Voyageur>();
		voyageurs.add(voyageur1);
		voyageurs.add(voyageur2);
		em.getTransaction().commit();
		voyageurDAO.mettreVoyageursDansItineraire(itineraire1, voyageurs);
		assertEquals(2, itineraire1.getVoyageurs().size());
	}

// TODO : Faire MajVoyageurDansItineraire avant de tester celle ci
//	@Test
//	void testMajVoyageursDansTrainAvecResa() {
//		List<Itineraire> listItineraire = itineraireDAO.getAllItineraires();
//		System.out.println(listItineraire.get(0).getId()); //--> id itineraire = 20
//		Itineraire itineraire = listItineraire.get(0);
//		List<Trajet> trajets = trajetDAO.getTrajetsByItineraire(itineraireDAO.getItineraireById(listItineraire.get(0).getId()));
//		System.out.println(trajets.size());
//		Set<Trajet> trajetsItineraire = new TreeSet<>(trajets);
//		Train train = trainDAO.getTrainById(1);
//		TrainAvecResa trainAvecResa = null;
//		List<Voyageur> list = new ArrayList<Voyageur>();
//		if(train instanceof TrainAvecResa) {
//			trainAvecResa = (TrainAvecResa) train;
//			list = trainAvecResa.getVoyageurs();
//			for (Voyageur v : list) {
//				System.out.println(v.getPrenom());
//			}
//			System.out.println(list.size());
//		}
//		voyageurDAO.majVoyageursDansTrainAvecResa(trainAvecResa, itineraire, trajetsItineraire);
//		System.out.println(trainAvecResa.getVoyageurs().size());
//	}

}
