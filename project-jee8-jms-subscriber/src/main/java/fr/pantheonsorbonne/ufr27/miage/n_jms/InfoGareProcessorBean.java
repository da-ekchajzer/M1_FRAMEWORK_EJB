package fr.pantheonsorbonne.ufr27.miage.n_jms;

import java.io.StringReader;
import java.util.List;

import javax.annotation.PostConstruct;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import fr.pantheonsorbonne.ufr27.miage.POJO.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.main.InfoGare;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.GareConcerneeJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ItineraireInfoJAXB;
import fr.pantheonsorbonne.ufr27.miage.n_mapper.ItineraireMapper;

public class InfoGareProcessorBean {

	@Inject
	private ConnectionFactory connectionFactory;

	@Inject
	@Named("ItineraireAckQueue")
	private Queue queueAck;

	@Inject
	@Named("ItinerairePubQueue")
	private Queue queueInfoPub;
	private Connection connection;

	private Session session;

	private MessageProducer producerAck;
	private MessageConsumer consumerInfoPub;

	private InfoGare infoGare;

	@PostConstruct
	private void init() {

		try {

			connection = connectionFactory.createConnection("projet", "inf2");
			connection.start();
			session = connection.createSession();
			consumerInfoPub = session.createConsumer(queueInfoPub);
			producerAck = session.createProducer(queueAck);

		} catch (JMSException e) {
			throw new RuntimeException("failed to create JMS Session", e);
		}

	}

	public void onInfoPubMessage(TextMessage message) throws JAXBException, JMSException {
		System.out.println(infoGare.getGare()+ " - " + " ReceivingInfoPub");
		JAXBContext context = JAXBContext.newInstance(GareConcerneeJAXB.class);
		StringReader reader = new StringReader(message.getText());

		String callout = message.getStringProperty("callout");
		String idItineraire = message.getStringProperty("idItineraire");

		GareConcerneeJAXB itineraireAck = (GareConcerneeJAXB) context.createUnmarshaller().unmarshal(reader);
		List<String> garesConcernee = itineraireAck.getGares();

		if (infoGareIsConcerned(garesConcernee)) {
			Message outgoingMessage = this.session.createMessage();
			outgoingMessage.setStringProperty("idItineraire", idItineraire);
			outgoingMessage.setStringProperty("gare", infoGare.getGare());

			switch (callout) {
			case "majItineraire":

				outgoingMessage.setStringProperty("callout", "majItineraire");

				break;

			case "createItineraire":

				outgoingMessage.setStringProperty("callout", "createItineraire");

				break;

			default:
				break;
			}

			Queue tmpQueue = session.createTemporaryQueue();
			outgoingMessage.setJMSReplyTo(tmpQueue);

			producerAck.send(outgoingMessage);

			ReceiveInfoItineraire(tmpQueue);

		}

	}

	private void ReceiveInfoItineraire(Queue tmpQueue) throws JMSException, JAXBException {
		System.out.println(infoGare.getGare()+ " - " + " ReceiveInfoItineraire");
		MessageConsumer consumer = session.createConsumer(tmpQueue);
		Message reply = consumer.receive();

		TextMessage message = (TextMessage) reply;
		
		//Traitement de la reponse
		JAXBContext context = JAXBContext.newInstance(ItineraireInfoJAXB.class);
		StringReader reader = new StringReader(message.getText());

		String callout = message.getStringProperty("callout");
		String idItineraire = message.getStringProperty("idItineraire");

		ItineraireInfoJAXB itineraireInfoJAXB = (ItineraireInfoJAXB) context.createUnmarshaller().unmarshal(reader);
		Itineraire itineraireInfoGare = ItineraireMapper.mapItineraireJAXBToItineraire(itineraireInfoJAXB,
				idItineraire);
		
		switch (callout) {
		
		case "majItineraire":
			infoGare.updateStop(itineraireInfoGare);
			break;

		case "createItineraire":
			infoGare.addStop(itineraireInfoGare);
			break;

		default:
			break;
		}

	}

	private boolean infoGareIsConcerned(List<String> garesConcernee) {
		for (String gare : garesConcernee) {
			if (gare.equals(infoGare.getGare())) {
				return true;
			}
		}

		return false;
	}

	public void consume() throws JMSException, JAXBException {
		onInfoPubMessage((TextMessage) consumerInfoPub.receive());
	}

	public void setInfoGare(InfoGare g) {
		this.infoGare = g;
	}

}
