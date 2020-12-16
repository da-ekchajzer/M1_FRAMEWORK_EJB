package fr.pantheonsorbonne.ufr27.miage.n_jms.conf;

import java.util.Hashtable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory;

@ApplicationScoped
public class JMSProducer {

	// fake JNDI context to create object
	private static final Context JNDI_CONTEXT;

	static {
		Hashtable<String, String> jndiBindings = new Hashtable<>();
		jndiBindings.put(Context.INITIAL_CONTEXT_FACTORY, ActiveMQInitialContextFactory.class.getName());
		jndiBindings.put("connectionFactory.ConnectionFactory", "tcp://localhost:61616");
		jndiBindings.put("app/jms/ItineraireAckQueue", "ItineraireAckQueue");
		jndiBindings.put("app/jms/ItineraireQueue", "InfoItineraireQueue");
		jndiBindings.put("app/jms/ItineraireAskQueue", "ItineraireAskQueue");

		Context c = null;
		try {
			c = new InitialContext(jndiBindings);
		} catch (NamingException e) {
			e.printStackTrace();
			c = null;
			System.exit(-1);

		} finally {
			JNDI_CONTEXT = c;
		}
	}

	@Produces
	@Named("ItineraireAckQueue")
	public Queue getItineraireAckQueue() throws NamingException {
		return (Queue) JNDI_CONTEXT.lookup("ItineraireAckQueue");
	}

	@Produces
	@Named("InfoItineraireQueue")
	public Queue getInfoItineraireQueue() throws NamingException {
		return (Queue) JNDI_CONTEXT.lookup("InfoItineraireQueue");
	}

	@Produces
	@Named("ItineraireAskQueue")
	public Queue getItineraireAskQueue() throws NamingException {
		return (Queue) JNDI_CONTEXT.lookup("ItineraireAskQueue");
	}

	@Produces
	public ConnectionFactory getJMSConnectionFactory() throws NamingException {
		return (ConnectionFactory) JNDI_CONTEXT.lookup("ConnectionFactory");
	}

}
