import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.concurrent.atomic.AtomicBoolean;

class SimpleMessageListener implements MessageListener {

    private final String name;
    private AtomicBoolean result;

    SimpleMessageListener(final String listener, AtomicBoolean result) {
        name = listener;
        this.result = result;
    }

    @Override
    public void onMessage(final Message msg) {
        TextMessage textMessage = (TextMessage) msg;
        try {
            String topicProp = msg.getStringProperty("topic");
            System.out.println("Receiver " + name +
                    " receives message [" +
                    textMessage.getText() +
                    "] with topic property: " +
                    topicProp);
            if (!topicProp.equals(name) && !name.equals("any")) {
                result.set(false);
            }
        } catch (JMSException e) {
            e.printStackTrace();
            result.set(false);
        }
    }
}
