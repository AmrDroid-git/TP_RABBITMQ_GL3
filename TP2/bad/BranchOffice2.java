import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.rabbitmq.client.MessageProperties;

public class BranchOffice2 {

    private static final String EXCHANGE_NAME = "sales_sync";

    public static void main(String[] args) {
        String dbUrl = "jdbc:mysql://localhost:3312/bo2_sales_db?useSSL=false";
        String dbUser = "root";
        String dbPassword = "root";
        String query = "SELECT * FROM Product_Sales";

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (
                Connection dbConnection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                PreparedStatement pst = dbConnection.prepareStatement(query);
                ResultSet rs = pst.executeQuery();

                com.rabbitmq.client.Connection mqConnection = factory.newConnection();
                Channel channel = mqConnection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            System.out.println("Connected to BO2 Database and RabbitMQ...");

            while (rs.next()) {
                String date = rs.getString("sale_date");
                String region = rs.getString("region");
                String product = rs.getString("product");
                int qty = rs.getInt("qty");
                double cost = rs.getDouble("cost");
                double amt = rs.getDouble("amt");
                double tax = rs.getDouble("tax");
                double total = rs.getDouble("total");

                String message = String.format("%s,%s,%s,%d,%f,%f,%f,%f",
                        date, region, product, qty, cost, amt, tax, total);

                channel.basicPublish(EXCHANGE_NAME, "sales", MessageProperties.PERSISTENT_TEXT_PLAIN,
                        message.getBytes("UTF-8"));
                // PERSISTENT_TEXT_PLAIN
                System.out.println(" [x] Sent to HO: '" + message + "'");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}