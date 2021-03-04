import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.log4j.BasicConfigurator;

import javax.jms.*;
import javax.naming.InitialContext;

public class TopicExample {

    //e. Отправка сообщения в топик
//f. Получение сообщения из топика несколькими подписчиками
    public static void main(final String[] args) throws Exception {
        BasicConfigurator.configure();
        Connection connection = null;
        InitialContext initialContext = null;
        try {

            initialContext = new InitialContext();
            Topic topic = (Topic) initialContext.lookup("topic/exampleTopic");
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://0.0.0.0:61616");
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageProducer producer = session.createProducer(topic);

            MessageConsumer messageConsumer1 = session.createConsumer(topic);
            MessageConsumer messageConsumer2 = session.createConsumer(topic);
            MessageConsumer messageConsumer3 = session.createConsumer(topic);

            TextMessage message = session.createTextMessage("Hello World");

            System.out.println("Sent message: " + message.getText());
            producer.send(message);
            connection.start();

            TextMessage messageReceived1 = (TextMessage) messageConsumer1.receive();
            System.out.println("Consumer 1 Received message: " + messageReceived1.getText());

            TextMessage messageReceived2 = (TextMessage) messageConsumer2.receive();
            System.out.println("Consumer 2 Received message: " + messageReceived2.getText());

            TextMessage messageReceived3 = (TextMessage) messageConsumer3.receive();
            System.out.println("Consumer 3 Received message: " + messageReceived3.getText());

        } finally {
            if (connection != null) {
                connection.close();
            }

            if (initialContext != null) {
                initialContext.close();
            }
        }
    }
}
