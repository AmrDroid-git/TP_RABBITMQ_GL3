import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class DataGeneratorBO2 {
    public static void main(String[] args) {
        String dbUrl = "jdbc:mysql://localhost:3312/bo2_sales_db?useSSL=false&allowPublicKeyRetrieval=true";
        String dbUser = "root";
        String dbPassword = "root";
        String region = "West"; // BO2 uses West

        String[] products = {"Paper", "Pens", "Laptops"};
        Map<String, Double> costMap = Map.of("Paper", 12.95, "Pens", 2.19, "Laptops", 999.99);

        Random rand = new Random();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d-MMM", Locale.ENGLISH);

        String insertSQL = "INSERT INTO product_sales (sale_date, region, product, qty, cost, amt, tax, total) VALUES (?,?,?,?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement ps = conn.prepareStatement(insertSQL)) {

            System.out.println("DataGeneratorBO2 connected to DB. Inserting every 10 seconds with incremental date...");
            LocalDate currentDate = LocalDate.now();

            while (true) {
                String saleDate = currentDate.format(fmt);
                String product = products[rand.nextInt(products.length)];
                double cost = costMap.get(product);
                int qty = rand.nextInt(100) + 1;
                double amt = Math.round((qty * cost) * 100.0) / 100.0;
                double taxRate = 0.07 + rand.nextDouble() * 0.03; // 7% - 10%
                double tax = Math.round(amt * taxRate * 100.0) / 100.0;
                double total = Math.round((amt + tax) * 100.0) / 100.0;

                ps.setString(1, saleDate);
                ps.setString(2, region);
                ps.setString(3, product);
                ps.setInt(4, qty);
                ps.setDouble(5, cost);
                ps.setDouble(6, amt);
                ps.setDouble(7, tax);
                ps.setDouble(8, total);
                ps.executeUpdate();

                System.out.println("Inserted BO2: " + saleDate + "," + region + "," + product + "," + qty + "," + cost + "," + amt + "," + tax + "," + total);

                Thread.sleep(10000);
                currentDate = currentDate.plusDays(1);
            }

        } catch (InterruptedException ie) {
            System.out.println("DataGeneratorBO2 interrupted, shutting down.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
