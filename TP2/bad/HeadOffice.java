import com.rabbitmq.client.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class HeadOffice {
    private static final String EXCHANGE_NAME = "sales_sync";

    public static void main(String[] argv) throws Exception {

        String dbUrl = "jdbc:mysql://localhost:3310/ho_sales_db?useSSL=false";
        String dbUser = "root";
        String dbPassword = "root";

        String insertSql = "INSERT INTO Product_Sales (sale_date, region, product, qty, cost, amt, tax, total) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection dbConnection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        PreparedStatement pst = dbConnection.prepareStatement(insertSql);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        com.rabbitmq.client.Connection mqConnection = factory.newConnection();
        Channel channel = mqConnection.createChannel();

        // Connect to exchange and bind a temporary queue using the "sales" routing key
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        String queueName = "durable_sales_queue";
        channel.queueDeclare(queueName, true, false, false, null);
        // durable = true bech kif yetsaker el office yab9a el queue fel server
        channel.queueBind(queueName, EXCHANGE_NAME, "sales");

        System.out.println(" [*] Head Office is waiting for sales data. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("\n [x] Received from Branch: '" + message + "'");

            try {
                String[] data = message.split(",");

                pst.setString(1, data[0]); // sale_date
                pst.setString(2, data[1]); // region
                pst.setString(3, data[2]); // product
                pst.setInt(4, Integer.parseInt(data[3])); // qty
                pst.setDouble(5, Double.parseDouble(data[4])); // cost
                pst.setDouble(6, Double.parseDouble(data[5])); // amt
                pst.setDouble(7, Double.parseDouble(data[6])); // tax
                pst.setDouble(8, Double.parseDouble(data[7])); // total

                // Execute the insert into the HO database
                pst.executeUpdate();
                System.out.println(" [v] Successfully saved to Master Database.");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                // hey bro c'est bon sajjelt fel db, tnajem tfas5ou mel queue

            } catch (Exception e) {
                System.err.println(" [!] Error saving to DB: " + e.getMessage());
            }
        };

        // Start consuming
        channel.basicConsume(queueName, false, deliverCallback, consumerTag -> {
        });
        // autoAck : false bech ken el mysql crashed, el message ma ydhi3ech yab9a fel
        // queue
    }
}