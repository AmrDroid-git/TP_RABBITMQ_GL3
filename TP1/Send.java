import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class Send {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            // queue name, durable(stays after closing or crash),
            // exclusive(Other
            // connections and clients are allowed to connect to this queue, send messages
            // to it, or consume from it), autodelete (The
            // queue will remain on the RabbitMQ server even if all consumers disconnect
            // from it)

            String message = "7mema";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());

            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}