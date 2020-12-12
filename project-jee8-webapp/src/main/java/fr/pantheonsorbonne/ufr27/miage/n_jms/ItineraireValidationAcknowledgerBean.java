package fr.pantheonsorbonne.ufr27.miage.n_jms;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.persistence.EntityManager;

public class ItineraireValidationAcknowledgerBean implements MessageListener {

	@ApplicationScoped
	@Inject
	EntityManager em;

	@Inject
	private ConnectionFactory connectionFactory;

	@Inject
	@Named("ItineraireAckQueue")
	private Queue queueAck;

	private Connection connection;

	private Session session;

	private MessageConsumer consumer;

	@PostConstruct
	private void init() {

		try {
			connection = connectionFactory.createConnection("projet", "inf2");
			connection.start();
			session = connection.createSession();
			consumer = session.createConsumer(queueAck);

			MessageListener listener = this;

			new Thread(new Runnable() {

				@Override
				public void run() {
					while (true) {
						try {
							Message message = consumer.receive();
							listener.onMessage(message);
						} catch (JMSException e) {
							e.printStackTrace();
						}
					}

				}
			}).start();

		} catch (JMSException e) {
			throw new RuntimeException("failed to create JMS Session", e);
		}

	}

	@Override
	public void onMessage(Message message) {
		// TODO
	}

}
