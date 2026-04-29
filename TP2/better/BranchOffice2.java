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
        String dbUrl = "jdbc:mysql://localhost:3312/bo2_sales_db?useSSL=false&allowPublicKeyRetrieval=true";
        String dbUser = "root";
        String dbPassword = "root";
        String query = "SELECT * FROM product_sales WHERE sync=0";

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (
                Connection dbConnection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                PreparedStatement pst = dbConnection.prepareStatement(query);
                ResultSet rs = pst.executeQuery();

                com.rabbitmq.client.Connection mqConnection = factory.newConnection();
                Channel channel = mqConnection.createChannel()) {

            System.out.println("Connected to BO2 Database and RabbitMQ...");
            
            int numberOfUpdatedRows = 0;

            while (rs.next()) {
                int sync = rs.getInt("sync");
                if(sync == 1) {
                    continue; // Skip already synced records
                }

                int id = rs.getInt("id");
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
                System.out.println(" [x] Sent to HO: '" + message + "'");
                String updateQuery = "UPDATE product_sales SET sync=1 WHERE id=?";
                PreparedStatement updateQueryPreparedStatement = dbConnection.prepareStatement(updateQuery);
                updateQueryPreparedStatement.setInt(1, id);
                numberOfUpdatedRows += updateQueryPreparedStatement.executeUpdate();
            }
            System.out.println(" [x] Number Of Updated Records: '" + numberOfUpdatedRows + "'");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}