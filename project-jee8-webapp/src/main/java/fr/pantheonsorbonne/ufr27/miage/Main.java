package fr.pantheonsorbonne.ufr27.miage;

import java.io.IOException;
import java.net.URI;
import java.util.Locale;

import javax.inject.Singleton;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.bridge.SLF4JBridgeHandler;

import fr.pantheonsorbonne.ufr27.miage.conf.EMFFactory;
import fr.pantheonsorbonne.ufr27.miage.conf.EMFactory;
import fr.pantheonsorbonne.ufr27.miage.conf.PersistenceConf;
import fr.pantheonsorbonne.ufr27.miage.dao.ArretDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.GareDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.IncidentDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrainDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.VoyageDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.VoyageurDAO;
import fr.pantheonsorbonne.ufr27.miage.exception.ExceptionMapper;
import fr.pantheonsorbonne.ufr27.miage.jms.ItineraireResponderBean;
import fr.pantheonsorbonne.ufr27.miage.jms.MessageGateway;
import fr.pantheonsorbonne.ufr27.miage.jms.conf.ConnectionFactorySupplier;
import fr.pantheonsorbonne.ufr27.miage.jms.conf.ItineraireAckQueueSupplier;
import fr.pantheonsorbonne.ufr27.miage.jms.conf.ItinerairePubQueueSupplier;
import fr.pantheonsorbonne.ufr27.miage.jms.conf.JMSProducer;
import fr.pantheonsorbonne.ufr27.miage.jms.utils.BrokerUtils;
import fr.pantheonsorbonne.ufr27.miage.repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.GareRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.IncidentRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.TrainRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.TrajetRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.VoyageRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.VoyageurRepository;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceIncident;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceItineraire;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceMajDecideur;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceMajExecuteur;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceMajInfoGare;
import fr.pantheonsorbonne.ufr27.miage.service.ServiceUtilisateur;
import fr.pantheonsorbonne.ufr27.miage.service.impl.BDDFillerServiceImpl;
import fr.pantheonsorbonne.ufr27.miage.service.impl.ServiceIncidentImp;
import fr.pantheonsorbonne.ufr27.miage.service.impl.ServiceItineraireImp;
import fr.pantheonsorbonne.ufr27.miage.service.impl.ServiceMajDecideurImp;
import fr.pantheonsorbonne.ufr27.miage.service.impl.ServiceMajExecuteurImp;
import fr.pantheonsorbonne.ufr27.miage.service.impl.ServiceMajInfoGareImp;
import fr.pantheonsorbonne.ufr27.miage.service.impl.ServiceUtilisateurImp;

/**
 * Main class.
 *
 */
public class Main {

	public static final String BASE_URI = "http://localhost:8080/";

	public static HttpServer startServer() {

		final ResourceConfig rc = new ResourceConfig()//
				.packages(true, "fr.pantheonsorbonne.ufr27.miage")//
				.register(DeclarativeLinkingFeature.class)//
				.register(JMSProducer.class).register(ExceptionMapper.class).register(PersistenceConf.class)
				.register(new AbstractBinder() {

					@Override
					protected void configure() {

						bindFactory(EMFFactory.class).to(EntityManagerFactory.class).in(Singleton.class);
						bindFactory(EMFactory.class).to(EntityManager.class).in(RequestScoped.class);
						bindFactory(ConnectionFactorySupplier.class).to(ConnectionFactory.class).in(Singleton.class);

						bind(ServiceIncidentImp.class).to(ServiceIncident.class);
						bind(ServiceItineraireImp.class).to(ServiceItineraire.class);
						bind(ServiceMajDecideurImp.class).to(ServiceMajDecideur.class);
						bind(ServiceMajExecuteurImp.class).to(ServiceMajExecuteur.class);
						bind(ServiceUtilisateurImp.class).to(ServiceUtilisateur.class);
						bind(ServiceMajInfoGareImp.class).to(ServiceMajInfoGare.class);

						bind(ArretDAO.class).to(ArretDAO.class);
						bind(GareDAO.class).to(GareDAO.class);
						bind(IncidentDAO.class).to(IncidentDAO.class);
						bind(ItineraireDAO.class).to(ItineraireDAO.class);
						bind(TrainDAO.class).to(TrainDAO.class);
						bind(TrajetDAO.class).to(TrajetDAO.class);
						bind(VoyageDAO.class).to(VoyageDAO.class);
						bind(VoyageurDAO.class).to(VoyageurDAO.class);

						bind(ArretRepository.class).to(ArretRepository.class);
						bind(GareRepository.class).to(GareRepository.class);
						bind(IncidentRepository.class).to(IncidentRepository.class);
						bind(ItineraireRepository.class).to(ItineraireRepository.class);
						bind(TrainRepository.class).to(TrainRepository.class);
						bind(TrajetRepository.class).to(TrajetRepository.class);
						bind(VoyageRepository.class).to(VoyageRepository.class);
						bind(VoyageurRepository.class).to(VoyageurRepository.class);

						bindFactory(ItineraireAckQueueSupplier.class).to(Queue.class).named("ItineraireAckQueue")
								.in(Singleton.class);
						bindFactory(ItinerairePubQueueSupplier.class).to(Queue.class).named("ItinerairePubQueue")
								.in(Singleton.class);

						bind(ItineraireResponderBean.class).to(ItineraireResponderBean.class).in(Singleton.class);
						bind(MessageGateway.class).to(MessageGateway.class).in(Singleton.class);

					}

				});
		return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
	}

	/**
	 * Main method.beanbeanbeanbean
	 * 
	 * @param args
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {

		Locale.setDefault(Locale.ENGLISH);
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
		final HttpServer server = startServer();

		PersistenceConf pc = new PersistenceConf();
		pc.getEM();
		pc.launchH2WS();

		BrokerUtils.startBroker();

		BDDFillerServiceImpl filler = new BDDFillerServiceImpl(pc.getEM());
		filler.fill();

		System.out.println(String.format(
				"Jersey app started with WADL available at " + "%sapplication.wadl\nHit enter to stop it...",
				BASE_URI));

		System.in.read();

		server.stop();
	}

}
