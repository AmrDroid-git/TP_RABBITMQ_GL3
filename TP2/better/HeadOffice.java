import com.rabbitmq.client.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class HeadOffice {
    public static void main(String[] argv) throws Exception {

        String dbUrl = "jdbc:mysql://localhost:3310/ho_sales_db?useSSL=false&allowPublicKeyRetrieval=true";
        String dbUser = "root";
        String dbPassword = "root";

        // We replaced the INSERT statement with a CALL to our new Stored Procedure
        String syncProcedure = "CALL SyncProductSale(?, ?, ?, ?, ?, ?, ?, ?)";

        Connection dbConnection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        // PreparedStatement works perfectly for calling stored procedures in MySQL
        PreparedStatement pst = dbConnection.prepareStatement(syncProcedure);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        com.rabbitmq.client.Connection mqConnection = factory.newConnection();
        Channel channel = mqConnection.createChannel();

        String queueName = "durable_sales_queue";

        System.out.println(" [*] Head Office is waiting for sales data. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("\n [x] Received from Branch: '" + message + "'");

            try {
                String[] data = message.split(",");

                pst.setString(1, data[0]); // p_date
                pst.setString(2, data[1]); // p_region
                pst.setString(3, data[2]); // p_product
                pst.setInt(4, Integer.parseInt(data[3])); // p_qty
                pst.setDouble(5, Double.parseDouble(data[4])); // p_cost
                pst.setDouble(6, Double.parseDouble(data[5])); // p_amt
                pst.setDouble(7, Double.parseDouble(data[6])); // p_tax
                pst.setDouble(8, Double.parseDouble(data[7])); // p_total

                // Execute the procedure
                pst.executeUpdate();
                System.out.println(" [v] Successfully synced (inserted/updated) to Master Database.");

                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

            } catch (Exception e) {
                System.err.println(" [!] Error saving to DB: " + e.getMessage());
            }
        };

        // Start consuming (autoAck is false for safety)
        channel.basicConsume(queueName, false, deliverCallback, consumerTag -> {
        });
    }
}