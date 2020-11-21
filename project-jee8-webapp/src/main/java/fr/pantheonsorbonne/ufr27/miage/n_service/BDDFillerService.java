package fr.pantheonsorbonne.ufr27.miage.n_service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jvnet.hk2.annotations.Service;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainSansResa;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Voyageur;

@Service
public class BDDFillerService {

	private EntityManager em;

	public BDDFillerService(EntityManager em) {
		this.em = em;
	}

	public void fill() {
		em.getTransaction().begin();

		// --------------------------------- Remplissage de la table Gare

		String[] nomGares = { "Paris - Gare de Lyon", "Avignon-Centre", "Aix en Provence", "Marseille - St Charles",
				"Dijon-Ville", "Lyon - Pardieu", "Narbonne", "Sete", "Perpignan", "Paris - Montparnasse", "Tours",
				"Bordeaux - Saint-Jean", "Pessac", "Arcachon-Centre", "Nantes" };

		Map<String, Gare> gares = new HashMap<>();
		for (String nomGare : nomGares) {
			Gare g = new Gare(nomGare);
			gares.put(nomGare, g);
			em.persist(g);
		}

		// --------------------------------- Remplissage de la table Voyageur

		String[] prenomsVoyageurs = { "Mariah", "Marc", "Sophia", "Alyssia", "Antoine", "Doudouh", "Lucie", "Lucas",
				"David", "Ben", "Maria", "Lucas", "Sophie", "Jean-Mi", "Jean", "Abdel", "Tatiana", "Charlotte",
				"Charlotte", "Abdel", "Ben", "Ben", "Mathieu", "Louis", "Jean-Luc", "Luc", "Jean", "Sophia", "Marc",
				"Manuel" };

		String[] nomsVoyageurs = { "Dupont", "Dupont", "Durand", "Martin", "Bernard", "Thomas", "Petit", "Grand",
				"Robert", "Richard", "Richard", "Dubois", "Petit", "Petit", "Moreau", "Laurent", "Simon", "Michel",
				"Lefevre", "Legrand", "Lefebvre", "Leroy", "Roux", "Leroi", "Morel", "Fournier", "Gerard", "Poirier",
				"Pommier", "Rossignol" };

		List<Voyageur> voyageursAyantReservesTrain3 = new ArrayList<>();
		List<Voyageur> voyageursAyantReservesTrain4 = new ArrayList<>();
		List<Voyageur> voyageursAyantReservesTrain5 = new ArrayList<>();
//		for (int i = 0; i < prenomsVoyageurs.length; i++) {
//			Voyageur v = new Voyageur(prenomsVoyageurs[i], nomsVoyageurs[i]);
//			if (i < 10)
//				voyageursAyantReservesTrain3.add(v);
//			else if (i >= 10 && i < 20)
//				voyageursAyantReservesTrain4.add(v);
//			else
//				voyageursAyantReservesTrain5.add(v);
//			em.persist(v);
//		}

		// --------------------------------- Remplissage de la table Train
		Train train1 = new TrainSansResa("TER");
		Train train2 = new TrainSansResa("TER");
		Train train3 = new TrainAvecResa(voyageursAyantReservesTrain3, "OUIGO");
		Train train4 = new TrainAvecResa(voyageursAyantReservesTrain4, "TGV");
		Train train5 = new TrainAvecResa(voyageursAyantReservesTrain5, "TGV");

		Train[] trains = { train1, train2, train3, train4, train5 };
		for (Train t : trains)
			em.persist(t);

		// --------------------------------- Remplissage de la table Arrêt

		Arret arret1 = new Arret(gares.get("Paris - Gare de Lyon"), null, LocalDateTime.now());
		Arret arret2 = new Arret(gares.get("Avignon-Centre"), LocalDateTime.now().plus(2, ChronoUnit.HOURS),
				LocalDateTime.now().plus(2, ChronoUnit.HOURS).plus(5, ChronoUnit.MINUTES));
		Arret arret3 = new Arret(gares.get("Aix en Provence"), LocalDateTime.now().plus(3, ChronoUnit.HOURS),
				LocalDateTime.now().plus(3, ChronoUnit.HOURS).plus(5, ChronoUnit.MINUTES));
		Arret arret4 = new Arret(gares.get("Marseille - St Charles"), LocalDateTime.now().plus(4, ChronoUnit.HOURS),
				null);

		Arret arret1_bis = new Arret(gares.get("Perpignan"), null, LocalDateTime.now());
		Arret arret2_bis = new Arret(gares.get("Sete"), LocalDateTime.now().plus(1, ChronoUnit.HOURS),
				LocalDateTime.now().plus(1, ChronoUnit.HOURS).plus(10, ChronoUnit.MINUTES));
		Arret arret3_bis = new Arret(gares.get("Narbonne"), LocalDateTime.now().plus(2, ChronoUnit.HOURS),
				LocalDateTime.now().plus(2, ChronoUnit.HOURS).plus(10, ChronoUnit.MINUTES));
		Arret arret4_bis = new Arret(gares.get("Marseille - St Charles"), LocalDateTime.now().plus(3, ChronoUnit.HOURS),
				null);

		Arret arret5 = new Arret(gares.get("Marseille - St Charles"), null,
				LocalDateTime.now().plus(4, ChronoUnit.HOURS));
		Arret arret6 = new Arret(gares.get("Dijon-Ville"), LocalDateTime.now().plus(5, ChronoUnit.HOURS),
				LocalDateTime.now().plus(5, ChronoUnit.HOURS).plus(5, ChronoUnit.MINUTES));
		Arret arret7 = new Arret(gares.get("Lyon - Pardieu"), LocalDateTime.now().plus(6, ChronoUnit.HOURS), null);

		Arret arret5_bis = new Arret(gares.get("Lyon - Pardieu"), null, LocalDateTime.now());
		Arret arret6_bis = new Arret(gares.get("Dijon-Ville"), LocalDateTime.now().plus(1, ChronoUnit.HOURS),
				LocalDateTime.now().plus(1, ChronoUnit.HOURS).plus(5, ChronoUnit.MINUTES));
		Arret arret7_bis = new Arret(gares.get("Marseille - St Charles"), LocalDateTime.now().plus(2, ChronoUnit.HOURS),
				null);

		Arret arret8 = new Arret(gares.get("Marseille - St Charles"), null,
				LocalDateTime.now().plus(3, ChronoUnit.HOURS));
		Arret arret9 = new Arret(gares.get("Narbonne"), LocalDateTime.now().plus(4, ChronoUnit.HOURS),
				LocalDateTime.now().plus(4, ChronoUnit.HOURS).plus(10, ChronoUnit.MINUTES));
		Arret arret10 = new Arret(gares.get("Sete"), LocalDateTime.now().plus(5, ChronoUnit.HOURS),
				LocalDateTime.now().plus(5, ChronoUnit.HOURS).plus(10, ChronoUnit.MINUTES));
		Arret arret11 = new Arret(gares.get("Perpignan"), LocalDateTime.now().plus(6, ChronoUnit.HOURS), null);

		Arret arret12 = new Arret(gares.get("Paris - Montparnasse"), null, LocalDateTime.now());
		Arret arret13 = new Arret(gares.get("Tours"), LocalDateTime.now().plus(1, ChronoUnit.HOURS),
				LocalDateTime.now().plus(1, ChronoUnit.HOURS).plus(5, ChronoUnit.MINUTES));
		Arret arret14 = new Arret(gares.get("Bordeaux - Saint-Jean"), LocalDateTime.now().plus(2, ChronoUnit.HOURS),
				null);

		Arret arret15 = new Arret(gares.get("Bordeaux - Saint-Jean"), null,
				LocalDateTime.now().plus(3, ChronoUnit.HOURS));
		Arret arret16 = new Arret(gares.get("Pessac"), LocalDateTime.now().plus(4, ChronoUnit.HOURS),
				LocalDateTime.now().plus(4, ChronoUnit.HOURS).plus(10, ChronoUnit.MINUTES));
		Arret arret17 = new Arret(gares.get("Arcachon-Centre"), LocalDateTime.now().plus(5, ChronoUnit.HOURS), null);

		Arret arret18 = new Arret(gares.get("Nantes"), null, LocalDateTime.now());
		Arret arret19 = new Arret(gares.get("Paris - Montparnasse"), LocalDateTime.now().plus(1, ChronoUnit.HOURS),
				null);

		Arret arret18_bis = new Arret(gares.get("Paris - Montparnasse"), null,
				LocalDateTime.now().plus(2, ChronoUnit.HOURS));
		Arret arret19_bis = new Arret(gares.get("Bordeaux - Saint-Jean"), LocalDateTime.now().plus(4, ChronoUnit.HOURS),
				null);

		Arret[] arrets = { arret1, arret2, arret3, arret4, arret1_bis, arret2_bis, arret3_bis, arret4_bis, arret5,
				arret6, arret7, arret5_bis, arret6_bis, arret7_bis, arret8, arret9, arret10, arret11, arret12, arret13,
				arret14, arret15, arret16, arret17, arret18, arret19, arret18_bis, arret19_bis };

		for (Arret a : arrets)
			em.persist(a);

		// --------------------------------- Remplissage de la table Itinéraire

		// Itineraire itineraire1

		// --------------------------------- Remplissage de la table Trajet

		Trajet trajet1 = new Trajet(gares.get("Paris - Gare de Lyon"), gares.get("Avignon-Centre"));
		Trajet trajet2 = new Trajet(gares.get("Avignon-Centre"), gares.get("Aix en Provence"));
		Trajet trajet3 = new Trajet(gares.get("Aix en Provence"), gares.get("Marseille - St Charles"));

		Trajet trajet1_bis = new Trajet(gares.get("Perpignan"), gares.get("Sete"));
		Trajet trajet2_bis = new Trajet(gares.get("Sete"), gares.get("Narbonne"));
		Trajet trajet3_bis = new Trajet(gares.get("Narbonne"), gares.get("Marseille - St Charles"));

		// Changement
		Trajet trajet4 = new Trajet(gares.get("Marseille - St Charles"), gares.get("Dijon-Ville"));
		Trajet trajet5 = new Trajet(gares.get("Dijon-Ville"), gares.get("Lyon - Pardieu"));

		Trajet trajet4_bis = new Trajet(gares.get("Lyon - Pardieu"), gares.get("Dijon-Ville"));
		Trajet trajet5_bis = new Trajet(gares.get("Dijon-Ville"), gares.get("Marseille - St Charles"));

		// Changement
		Trajet trajet6 = new Trajet(gares.get("Marseille - St Charles"), gares.get("Narbonne"));
		Trajet trajet7 = new Trajet(gares.get("Narbonne"), gares.get("Sete"));
		Trajet trajet8 = new Trajet(gares.get("Sete"), gares.get("Perpignan"));

		Trajet trajet9 = new Trajet(gares.get("Paris - Montparnasse"), gares.get("Tours"));
		Trajet trajet10 = new Trajet(gares.get("Tours"), gares.get("Bordeaux - Saint-Jean"));

		// Changement
		Trajet trajet11 = new Trajet(gares.get("Bordeaux - Saint-Jean"), gares.get("Pessac"));
		Trajet trajet12 = new Trajet(gares.get("Pessac"), gares.get("Arcachon-Centre"));

		Trajet trajet13 = new Trajet(gares.get("Nantes"), gares.get("Paris - Montparnasse"));

		// Changement
		Trajet trajet14 = new Trajet(gares.get("Paris - Montparnasse"), gares.get("Bordeaux - Saint-Jean"));

		Trajet[] trajets = { trajet1, trajet2, trajet3, trajet4, trajet5, trajet6, trajet7, trajet8, trajet9, trajet10,
				trajet11, trajet12, trajet13, trajet14 };

		for (Trajet t : trajets)
			em.persist(t);

		// --------------------------------- Remplissage de la table Voyage

		em.getTransaction().commit();
	}
}
