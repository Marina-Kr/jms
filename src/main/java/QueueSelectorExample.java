import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.log4j.BasicConfigurator;

import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import java.util.concurrent.atomic.AtomicBoolean;

//d. Выборка сообщения по селектору
public class QueueSelectorExample {

    public static void main(final String[] args) throws Exception {
        BasicConfigurator.configure();
        AtomicBoolean result = new AtomicBoolean(true);
        Connection connection = null;
        InitialContext initialContext = null;
        try {
            initialContext = new InitialContext();
            Queue queue = (Queue) initialContext.lookup("queue/exampleQueue");

            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://0.0.0.0:61616");
            connection = connectionFactory.createConnection();
            connection.start();

            Session senderSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = senderSession.createProducer(queue);

            String selector1 = "topic='work'";
            String selector2 = "topic='news'";

            Session session1 = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer1 = session1.createConsumer(queue, selector1);
            consumer1.setMessageListener(new SimpleMessageListener("work", result));

            Session session2 = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer2 = session2.createConsumer(queue, selector2);
            consumer2.setMessageListener(new SimpleMessageListener("news", result));

            Session anySession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer anyConsumer = anySession.createConsumer(queue);
            anyConsumer.setMessageListener(new SimpleMessageListener("any", result));


            TextMessage message1 = senderSession.createTextMessage("work");
            message1.setStringProperty("topic", "work");
            TextMessage message2 = senderSession.createTextMessage("news");
            message2.setStringProperty("topic", "news");
            TextMessage message3 = senderSession.createTextMessage("spam");
            message3.setStringProperty("topic", "spam");

            producer.send(message1);
            System.out.println("Message sent: " + message1.getText());
            producer.send(message2);
            System.out.println("Message sent: " + message2.getText());
            producer.send(message3);
            System.out.println("Message sent: " + message3.getText());

            Thread.sleep(5000);

            if (!result.get())
                throw new IllegalStateException();
        } finally {

            if (initialContext != null) {
                initialContext.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
}


