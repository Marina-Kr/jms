import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.log4j.BasicConfigurator;

import javax.jms.*;
import javax.naming.InitialContext;

// a. Отправка сообщения в очередь
// b. Получение сообщения из очереди
// c. Заполнение заголовка отправляемого сообщения
public class QueueExample {

    public static void main(final String[] args) throws Exception {
        BasicConfigurator.configure();
        Connection connection = null;
        InitialContext initialContext = null;
        try {

            initialContext = new InitialContext();
            Queue queue = (Queue) initialContext.lookup("queue/exampleQueue");
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://0.0.0.0:61616");
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageProducer producer = session.createProducer(queue);
            MessageConsumer messageConsumer = session.createConsumer(queue);

            TextMessage message = session.createTextMessage("Hello World");
            message.setStringProperty("status", "new");
            System.out.println("Sent message: " + message.getText());
            producer.send(message);
            connection.start();

            TextMessage messageReceived = (TextMessage) messageConsumer.receive();
            System.out.println("Consumer Received message: " + messageReceived.getText());


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
