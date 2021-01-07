package fr.pantheonsorbonne.ufr27.miage.n_jms;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.persistence.EntityManager;
import javax.xml.bind.JAXBException;

@ApplicationScoped
public class ItineraireResponderBean implements MessageListener{

	@Inject
	EntityManager em;
	
	@Inject
	MessageGateway messageGateway;

	@PostConstruct
	private void init() {
		messageGateway = new MessageGateway();
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Message msg = messageGateway.ackInfogare();
						onMessage(msg);
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}

			}
		}).start();

	}

	@Override
	public void onMessage(Message incomingRequest) {
		try {
			messageGateway.ackReplyItineraire(incomingRequest);
		} catch (JMSException | JAXBException e) {
			e.printStackTrace();
		}
	}

}
