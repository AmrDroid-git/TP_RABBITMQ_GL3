
// Import the necessary classes from the RabbitMQ Java Client library
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Receive {

    // Define the name of the queue we want to listen to. It must match the
    // producer's queue name.
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {

        // 1. Set up the ConnectionFactory. This object configures the connection to the
        // server.
        ConnectionFactory factory = new ConnectionFactory();

        // Tell the factory to connect to the RabbitMQ node running on the local
        // machine.
        factory.setHost("localhost");

        // 2. Open a network connection to the RabbitMQ server.
        Connection connection = factory.newConnection();

        // 3. Create a channel. Most of the API for getting things done resides in the
        // channel.
        Channel channel = connection.createChannel();

        // 4. Declare the queue. We do this here as well as in the sender code
        // to ensure the queue exists before we try to consume messages from it.
        // Parameters: (queue_name, durable, exclusive, autoDelete, arguments)
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // Print a message to the console so we know the application is running and
        // waiting.
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // 5. Create a DeliverCallback.
        // Since RabbitMQ pushes messages to us asynchronously, we need to provide a
        // callback
        // object that will buffer and handle the messages as they arrive.
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {

            // Extract the message body (which is a byte array) and convert it back into a
            // String using UTF-8 encoding.
            String message = new String(delivery.getBody(), "UTF-8");

            // Print the received message to the console.
            System.out.println(" [x] Received '" + message + "'");
        };

        // 6. Start consuming messages from the queue.
        // Parameters: (queue_name, autoAck, deliverCallback, cancelCallback)
        // 'true' means auto-acknowledgment is turned on (we automatically tell RabbitMQ
        // we received the message).
        // The last parameter is an empty callback for when a consumer is canceled
        // (e.g., if the queue is deleted).
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
        });
    }
}