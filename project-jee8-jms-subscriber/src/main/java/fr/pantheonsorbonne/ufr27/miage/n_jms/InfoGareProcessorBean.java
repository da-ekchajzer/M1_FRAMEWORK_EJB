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
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ItineraireInfoGareJAXB;
import fr.pantheonsorbonne.ufr27.miage.n_mapper.ItineraireMapper;

public class InfoGareProcessorBean {

	@Inject
	private ConnectionFactory connectionFactory;

	@Inject
	@Named("ItineraireAckQueue")
	private Queue queueAck;

	@Inject
	@Named("InfoItineraireQueue")
	private Queue queueInfo;

	@Inject
	@Named("ItineraireAskQueue")
	private Queue queueAsk;

	private Connection connection;

	private Session session;

	private MessageConsumer consumerAck;
	private MessageConsumer consumerInfo;
	private MessageProducer producerAsk;

	private InfoGare infoGare;

	@PostConstruct
	private void init() {

		try {

			connection = connectionFactory.createConnection("nicolas", "nicolas");
			connection.start();
			session = connection.createSession();
			consumerInfo = session.createConsumer(queueInfo);
			consumerAck = session.createConsumer(queueAck);
			producerAsk = session.createProducer(queueAsk);

		} catch (JMSException e) {
			throw new RuntimeException("failed to create JMS Session", e);
		}

	}

	public void onAckMessage(TextMessage message) throws JAXBException, JMSException {
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

				outgoingMessage.setStringProperty("callout", "getInfoMajItineraire");

				break;

			case "CreateItineraire":

				outgoingMessage.setStringProperty("callout", "getInfoCreationItineraire");

				break;

			default:
				break;
			}
			producerAsk.send(outgoingMessage);
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

	private void onInfoMessage(TextMessage message) throws JAXBException, JMSException {
		JAXBContext context = JAXBContext.newInstance(ItineraireInfoGareJAXB.class);
		StringReader reader = new StringReader(message.getText());

		String callout = message.getStringProperty("callout");
		String idItineraire = message.getStringProperty("idItineraire");

		ItineraireInfoGareJAXB itineraireInfoGareJAXB = (ItineraireInfoGareJAXB) context.createUnmarshaller().unmarshal(reader);
		Itineraire itineraireInfoGare = ItineraireMapper.mapItineraireJAXBToItineraire(itineraireInfoGareJAXB, idItineraire);
		switch (callout) {
			case "majItineraire":
				infoGare.updateStop(itineraireInfoGare);
				break;
	
			case "CreateItineraire":
				infoGare.addStop(itineraireInfoGare);
				break;
	
			default:
				break;
			}
	}


	public void consume(InfoGare g) throws JMSException, JAXBException {
		this.infoGare = g;
		onAckMessage((TextMessage) consumerAck.receive());
		onInfoMessage((TextMessage) consumerInfo.receive());
	}

}
