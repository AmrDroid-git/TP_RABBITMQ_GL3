import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class SetupRabbitMQ {
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {

            System.out.println(" [*] Setting up RabbitMQ Infrastructure...");

            // 1. Create the Exchange
            String exchangeName = "sales_sync";
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);
            System.out.println(" [v] Exchange '" + exchangeName + "' created.");

            // 2. Create the Durable Queue
            String queueName = "durable_sales_queue";
            channel.queueDeclare(queueName, true, false, false, null);
            System.out.println(" [v] Durable Queue '" + queueName + "' created.");

            // 3. Bind the Queue to the Exchange with the routing key "sales"
            channel.queueBind(queueName, exchangeName, "sales");
            System.out.println(" [v] Queue bound to Exchange with routing key 'sales'.");

            System.out.println(" [*] Setup Complete! You can now run your branch and head office apps.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}