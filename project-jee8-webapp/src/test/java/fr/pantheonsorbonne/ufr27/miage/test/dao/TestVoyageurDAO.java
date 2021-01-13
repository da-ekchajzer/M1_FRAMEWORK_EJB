package fr.pantheonsorbonne.ufr27.miage.test.dao;

import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import fr.pantheonsorbonne.ufr27.miage.dao.ArretDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.GareDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.ItineraireDAO.MulitpleResultsNotExpectedException;
import fr.pantheonsorbonne.ufr27.miage.dao.TrainDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.VoyageDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.VoyageurDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.VoyageurDAO.TrainSansResaNotExpectedException;
import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.jpa.TrainSansResa;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.jpa.Voyage;
import fr.pantheonsorbonne.ufr27.miage.jpa.Voyageur;
import fr.pantheonsorbonne.ufr27.miage.repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.TrajetRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.VoyageRepository;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
@TestMethodOrder(OrderAnnotation.class)
public class TestVoyageurDAO {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator
			.from(VoyageurDAO.class, ItineraireRepository.class, TrajetRepository.class, ItineraireDAO.class,
					VoyageRepository.class, TrajetDAO.class, TrainDAO.class, VoyageDAO.class, ArretRepository.class,
					ArretDAO.class, TrajetDAO.class, GareDAO.class, TestPersistenceProducer.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	VoyageurDAO voyageurDAO;
	@Inject
	VoyageDAO voyageDAO;
	@Inject
	TrainDAO trainDAO;
	@Inject
	TrajetDAO trajetDAO;
	@Inject
	ItineraireDAO itineraireDAO;
	@Inject
	GareDAO gareDAO;
	@Inject
	ArretDAO arretDAO;
	
	private static List<Object> objectsToDelete; 

	@BeforeAll
	public void setup() {
		objectsToDelete = new ArrayList<Object>();
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
			objectsToDelete.add(g);
		}

		// --------------------------------- Remplissage de la table Train
		Train train1 = new TrainAvecResa("TGV", "T1");
		Train train2 = new TrainSansResa("TER", "T2");
		Train train3 = new TrainAvecResa("OUIGO", "T3");
		Train train4 = new TrainAvecResa("OUIGO", "T4");
		Train train5 = new TrainSansResa("TER", "T5");
		Train train6 = new TrainAvecResa("TGV", "T6");
		Train train7 = new TrainSansResa("TER", "T7");
		Train train8 = new TrainAvecResa("TGV", "T8");
		Train train9 = new TrainAvecResa("TGV", "T9");

		Train[] trains = { train1, train2, train3, train4, train5, train6, train7, train8, train9 };
		for (Train t : trains) {
			em.persist(t);
			objectsToDelete.add(t);
		}
		// --------------------------------- Arrêts

		Arret arret1 = new Arret(gares.get("Paris - Gare de Lyon"), null,
				LocalDateTime.now().plus(20, ChronoUnit.SECONDS));
		Arret arret2 = new Arret(gares.get("Avignon-Centre"), LocalDateTime.now().plus(1, ChronoUnit.MINUTES),
				LocalDateTime.now().plus(1, ChronoUnit.MINUTES).plus(30, ChronoUnit.SECONDS));
		Arret arret3 = new Arret(gares.get("Aix en Provence"), LocalDateTime.now().plus(3, ChronoUnit.MINUTES),
				LocalDateTime.now().plus(3, ChronoUnit.MINUTES).plus(1, ChronoUnit.MINUTES));
		Arret arret4 = new Arret(gares.get("Marseille - St Charles"), LocalDateTime.now().plus(5, ChronoUnit.MINUTES),
				null);

		Arret[] arrets = { arret1, arret2, arret3, arret4 };

		for (Arret a : arrets) {
			em.persist(a);
			objectsToDelete.add(a);
		}
		// --------------------------------- Remplissage de la table Itinéraire

		Itineraire itineraire1 = new Itineraire(train1, "IT1");
		itineraire1.addArret(arret1);
		itineraire1.setArretActuel(arret1);
		itineraire1.addArret(arret2);
		itineraire1.addArret(arret3);
		itineraire1.addArret(arret4);

		em.persist(itineraire1);
		objectsToDelete.add(itineraire1);

		// --------------------------------- Remplissage de la table Trajet

		Trajet trajet1 = new Trajet(gares.get("Paris - Gare de Lyon"), gares.get("Avignon-Centre"), itineraire1, 0);
		Trajet trajet2 = new Trajet(gares.get("Avignon-Centre"), gares.get("Aix en Provence"), itineraire1, 1);
		Trajet trajet3 = new Trajet(gares.get("Aix en Provence"), gares.get("Marseille - St Charles"), itineraire1, 2);

		Trajet[] trajets = { trajet1, trajet2, trajet3 };

		for (Trajet t : trajets) {
			em.persist(t);
			objectsToDelete.add(t);	
		}

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
		objectsToDelete.add(voyage1);
		objectsToDelete.add(voyage2);

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
			if (i < 5) {
				voyage1.addVoyageur(v);
				v.setVoyageActuel(voyage1);
			}
			if (i >= 5 && i < 8) {
				voyage2.addVoyageur(v);
				v.setVoyageActuel(voyage2);
			}
			em.persist(v);
			objectsToDelete.add(v);
		}

		em.getTransaction().commit();
	}

	@Test
	@Order(1)
	void testGetVoyageursByVoyageActuel() {
		Voyage voyage1 = voyageDAO.getAllVoyages().get(0);
		List<Voyageur> voyageurs = voyageurDAO.getVoyageursByVoyageActuel(voyage1);
		assertEquals(5, voyageurs.size());
	}

	@Test
	@Order(2)
	void testMettreVoyageursDansItineraire() {
		Itineraire itineraire1 = itineraireDAO.getItineraireByBusinessId("IT1");
		Voyage voyage1 = voyageDAO.getAllVoyages().get(0);
		voyageurDAO.mettreVoyageursDansItineraire(itineraire1, voyage1.getVoyageurs());
		assertEquals(voyage1.getVoyageurs().size(), itineraire1.getVoyageurs().size());
	}

	@Test
	@Order(3)
	void testMajVoyageursDansTrainAvecResa() throws MulitpleResultsNotExpectedException {
		Train train = trainDAO.getTrainByBusinessId("T1");
		Itineraire itineraire1 = itineraireDAO.getItineraireByTrainEtEtat(train.getId(),
				CodeEtatItinieraire.EN_ATTENTE);
		List<Trajet> trajets = trajetDAO.getTrajetsByItineraire(itineraire1);
		Set<Trajet> trajetsItineraire = new TreeSet<>(trajets);
		TrainAvecResa trainAvecResa = null;
		if (train instanceof TrainAvecResa) {
			trainAvecResa = (TrainAvecResa) train;
		}
		int prevSize = trainAvecResa.getVoyageurs().size();
		try {
			voyageurDAO.majVoyageursDansTrainAvecResa(trainAvecResa, itineraire1, trajetsItineraire);
		} catch (TrainSansResaNotExpectedException e) {
			e.printStackTrace();
		}
		assertTrue(trainAvecResa.getVoyageurs().size() > prevSize);
	}
	
	@Test
	void testNbTrains() {
		assertEquals(9, trainDAO.getAllTrains().size());
	}
	
	// TODO !!!
	@AfterAll
	void nettoyageDonnees() {
		System.out.println(itineraireDAO.getAllItineraires().size() + " itinéraires");
		System.out.println(arretDAO.getAllArrets().size() + " arrêts");
		System.out.println(trainDAO.getAllTrains().size() + " trains");
		System.out.println(voyageurDAO.getAllVoyageurs().size() + " voyageurs");
		System.out.println(voyageDAO.getAllVoyages().size() + " voyages");
		System.out.println(trajetDAO.getAllTrajets().size() + " trajets");

		em.getTransaction().begin();
		
		/**
		 * 1ère méthode : on parcourt une liste static qui contient l'ensemble des entités
		 * qui ont été créées et persistées dans CETTE classe de test
		 * (on la parcourt à l'envers pour respecter l'ordre de suppression en BD :
		 * on doit supprimer un Arrêt avant de supprimer une Gare)
		 * => Ne fonctionne pas
		 */
//		for(int i = objectsToDelete.size()-1 ; i >= 0 ; i--) {
// 			if(!em.contains(objectsToDelete.get(i))) {
// 				em.merge(objectsToDelete.get(i));
// 			}
//			em.remove(objectsToDelete.get(i));
//		}
		
		/**
		 * 2ème méthode : 
		 * l'EntityManager ne contient que les objets créés dans cette classe (c'est ok ça a été testé)
		 * car chaque classe supprime les objets qu'elle a utilisé à sa fin donc si on utilise les
		 * méthodes "getAll" des DAOs, on peut récupérer toutes les entités créées puis les supprimer.
		 * Problème :
		 * L'ordre suivant de suppression pourrait être le bon sauf que pour supprimer un Voyageur,
		 * il faut avoir supprimer le/les Itineraire(s) associé(s). Si on essaye de supprimer les Itineraires
		 * en premier, on aura le même problème entre les objets Itineraire et Trajet (pour supprimer un Itineraire
		 * il faut supprimer le/les trajet(s) associé(s)) et ainsi de suite..
		 * Il y a une "dépendance cyclique" entre nos objets qui empêche toutes suppressions.
		 * 
		 * On a pas ce problème dans les autres classes de test car il apparaît dès lors qu'on persiste des Voyageurs
		 * (l'objet Voyageur étant l'entité "la plus grande") ce qu'on ne fait jamais dans les autres classes DAOs.
		 * 
		 * Selon moi il faut modifier le schéma de notre BD pour supprimer cette dépendance cyclique entre
		 * les entités.
		 */
		for(Voyageur voyageur : this.voyageurDAO.getAllVoyageurs()) {
			em.remove(voyageur);
		}
		for(Voyage voyage : this.voyageDAO.getAllVoyages()) {
			em.remove(voyage);
		}
		for(Trajet trajet : this.trajetDAO.getAllTrajets()) {
			em.remove(trajet);
		}
		for(Itineraire itineraire : this.itineraireDAO.getAllItineraires()) {
			em.remove(itineraire);
		}
		for(Arret arret : this.arretDAO.getAllArrets()) {
			em.remove(arret);
		}
		for(Train train : this.trainDAO.getAllTrains()) {
			em.remove(train);
		}
		for(Gare gare : this.gareDAO.getAllGares()) {
			em.remove(gare);
		}
		em.getTransaction().commit();
		System.out.println(itineraireDAO.getAllItineraires().size() + " itinéraires");
		System.out.println(arretDAO.getAllArrets().size() + " arrêts");
		System.out.println(trainDAO.getAllTrains().size() + " trains");
		System.out.println(voyageurDAO.getAllVoyageurs().size() + " voyageurs");
		System.out.println(voyageDAO.getAllVoyages().size() + " voyages");
		System.out.println(trajetDAO.getAllTrajets().size() + " trajets");
	}
	
}
