package fr.pantheonsorbonne.ufr27.miage.jms;

import java.io.StringWriter;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.GaresConcerneesJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.InfosItineraireJAXB;
import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.mapper.MapperUtils;
import fr.pantheonsorbonne.ufr27.miage.repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.ItineraireRepository;

@ManagedBean
@ApplicationScoped
public class MessageGateway {

	@Inject
	private ConnectionFactory connectionFactory;

	@Inject
	@Named("ItineraireAckQueue")
	private Queue queueAck;

	@Inject
	@Named("ItinerairePubQueue")
	private Queue queueInfoPub;

	private MessageConsumer consumerAck;
	private MessageProducer producerInfoPub;
	private MessageProducer producerAckReply;

	private Topic defaultTopic;

	private Connection connection;

	private Session session;

	@Inject
	ItineraireRepository itineraireRepository;

	@Inject
	ArretRepository arretRepository;

	@PostConstruct
	private void init() {

		try {
			connection = connectionFactory.createConnection("projet", "inf2");
			connection.start();
			session = connection.createSession();

			defaultTopic = session.createTopic("publishItineraire");

			consumerAck = session.createConsumer(queueAck);
			producerInfoPub = session.createProducer(defaultTopic);
			producerAckReply = session.createProducer(null);

		} catch (JMSException e) {
			throw new RuntimeException("failed to create JMS Session", e);
		}
	}

	public void ackReplyItineraire(Message incomingRequest) throws JMSException, JAXBException {
		String idItineraire = incomingRequest.getStringProperty("idItineraire");
		String nomGare = incomingRequest.getStringProperty("gare");

		JAXBContext jaxbContext = JAXBContext.newInstance(InfosItineraireJAXB.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		InfosItineraireJAXB itineraireInfoJAXB = new InfosItineraireJAXB();

		Itineraire i = itineraireRepository.getItineraireByBusinessId(idItineraire);
		Arret a = arretRepository.getArretParItineraireEtNomGare(i, nomGare);
		itineraireInfoJAXB.setEtatItineraire(i.getEtat());

		if (a.getHeureArriveeEnGare() != null) {
			itineraireInfoJAXB
					.setHeureArrivee(MapperUtils.localDateTimeToXmlGregorianCalendar(a.getHeureArriveeEnGare()));
		}
		if (a.getHeureDepartDeGare() != null) {
			itineraireInfoJAXB
					.setHeureDepart(MapperUtils.localDateTimeToXmlGregorianCalendar(a.getHeureDepartDeGare()));
		}

		itineraireInfoJAXB
				.setGareArrivee(i.getArretsDesservis().get(i.getArretsDesservis().size() - 1).getGare().getNom());
		itineraireInfoJAXB.setGareDepart(i.getArretsDesservis().get(0).getGare().getNom());

		for (Arret arretDesservis : i.getArretsDesservis()) {
			if (arretDesservis.isAfter(a)) {
				itineraireInfoJAXB.getGaresDesservies().add(arretDesservis.getGare().getNom());
			}
		}
		StringWriter writer = new StringWriter();
		jaxbMarshaller.marshal(itineraireInfoJAXB, writer);

		TextMessage reply = session.createTextMessage(writer.toString());
		reply.setStringProperty("callout", incomingRequest.getStringProperty("callout"));
		reply.setStringProperty("idItineraire", incomingRequest.getStringProperty("idItineraire"));
		reply.setJMSCorrelationID(incomingRequest.getJMSMessageID());

		producerAckReply.send(incomingRequest.getJMSReplyTo(), reply);
	}

	public Message ackInfogare() throws JMSException {

		Message request = consumerAck.receive();
		return request;
	}

	public void publishCreation(Itineraire itineraire) {
		try {
			publishItineraire(itineraire, "createItineraire");
		} catch (JAXBException | JMSException e) {
			e.printStackTrace();
		}
	}

	public void publishMaj(Itineraire itineraire) {
		try {
			publishItineraire(itineraire, "majItineraire");
		} catch (JAXBException | JMSException e) {
			e.printStackTrace();
		}
	}

	private void publishItineraire(Itineraire itineraire, String callout) throws JAXBException, JMSException {
		JAXBContext jaxbContext = JAXBContext.newInstance(GaresConcerneesJAXB.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		GaresConcerneesJAXB gareConcerneeJAXB = new GaresConcerneesJAXB();

		for (Arret a : itineraire.getArretsDesservis()) {
			if (a.equals(itineraire.getArretActuel()) || itineraire.getArretActuel().isBefore(a)) {
				gareConcerneeJAXB.getGares().add(a.getGare().getNom());
			}
		}

		StringWriter writer = new StringWriter();
		jaxbMarshaller.marshal(gareConcerneeJAXB, writer);

		TextMessage publishMessage = session.createTextMessage(writer.toString());
		publishMessage.setStringProperty("idItineraire", itineraire.getBusinessId());
		publishMessage.setStringProperty("callout", callout);

		producerInfoPub.send(publishMessage);
	}

}
