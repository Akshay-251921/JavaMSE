package org.example;
import java.sql.*;
import java.util.Scanner;

public class RetailStore {

    static final String URL =
            "jdbc:mysql://localhost:3306/retail_db2";
    static final String USER = "root";
    static final String PASSWORD = "Aks@25192159";

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        try (Connection con =
                     DriverManager.getConnection(URL, USER, PASSWORD)) {

            System.out.println("Database Connected Successfully!");

            while (true) {

                System.out.println("\n===== RETAIL STORE INVENTORY =====");
                System.out.println("1. Insert Product");
                System.out.println("2. Display Products by Category (Quantity < 20)");
                System.out.println("3. Update Stock");
                System.out.println("4. Update Product Price");
                System.out.println("5. Calculate Total Inventory Value");
                System.out.println("6. Delete Product (Only if Quantity = 0)");
                System.out.println("7. Exit");

                System.out.print("Enter your choice: ");
                int choice = sc.nextInt();

                switch (choice) {

                    case 1:
                        insertProduct(con);
                        break;

                    case 2:
                        displayProducts(con);
                        break;

                    case 3:
                        updateStock(con);
                        break;

                    case 4:
                        updatePrice(con);
                        break;

                    case 5:
                        totalInventoryValue(con);
                        break;

                    case 6:
                        deleteProduct(con);
                        break;

                    case 7:
                        System.out.println("Thank you!");
                        return;

                    default:
                        System.out.println("Invalid Choice!");
                }
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    // 1. Insert Product
    static void insertProduct(Connection con) throws SQLException {

        String sql = "INSERT INTO Product VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.print("Product ID: ");
            int id = sc.nextInt();
            sc.nextLine();

            System.out.print("Product Name: ");
            String name = sc.nextLine();

            System.out.print("Category: ");
            String category = sc.nextLine();

            System.out.print("Quantity: ");
            int quantity = sc.nextInt();

            System.out.print("Price: ");
            double price = sc.nextDouble();

            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, category);
            ps.setInt(4, quantity);
            ps.setDouble(5, price);

            ps.executeUpdate();

            System.out.println("Product Inserted Successfully!");
        }
    }

    // 2. Display products by category with quantity < 20
    static void displayProducts(Connection con) throws SQLException {

        String sql = "SELECT * FROM Product " +
                "WHERE category = ? AND quantity < 20 " +
                "ORDER BY price DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            sc.nextLine();
            System.out.print("Enter Category: ");
            String category = sc.nextLine();

            ps.setString(1, category);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    System.out.println(
                            rs.getInt("product_id") + " | " +
                                    rs.getString("product_name") + " | " +
                                    rs.getString("category") + " | " +
                                    rs.getInt("quantity") + " | " +
                                    rs.getDouble("price")
                    );
                }
            }
        }
    }

    // 3. Update Stock
    static void updateStock(Connection con) throws SQLException {

        String sql = "UPDATE Product SET quantity = quantity + ? " +
                "WHERE product_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.print("Enter additional quantity: ");
            int quantity = sc.nextInt();

            System.out.print("Enter Product ID: ");
            int id = sc.nextInt();

            ps.setInt(1, quantity);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();

            System.out.println(rows > 0 ?
                    "Stock Updated!" : "Product Not Found!");
        }
    }

    // 4. Update Product Price
    static void updatePrice(Connection con) throws SQLException {

        System.out.print("Enter Product ID: ");
        int id = sc.nextInt();

        System.out.print("Enter New Price: ");
        double price = sc.nextDouble();

        if (price <= 0) {
            System.out.println("Price must be positive!");
            return;
        }

        String sql = "UPDATE Product SET price = ? WHERE product_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, price);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Price Updated Successfully!");

                String query = "SELECT * FROM Product WHERE product_id = ?";

                try (PreparedStatement p = con.prepareStatement(query)) {
                    p.setInt(1, id);

                    try (ResultSet rs = p.executeQuery()) {
                        if (rs.next()) {
                            System.out.println(
                                    "Updated Product: " +
                                            rs.getString("product_name") +
                                            " | Price: " + rs.getDouble("price")
                            );
                        }
                    }
                }
            } else {
                System.out.println("Product Not Found!");
            }
        }
    }

    // 5. Calculate Total Inventory Value
    static void totalInventoryValue(Connection con) throws SQLException {

        String sql = "SELECT SUM(quantity * price) AS total FROM Product";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                System.out.println(
                        "Total Inventory Value: " + rs.getDouble("total")
                );
            }
        }
    }

    // 6. Delete Product only if quantity = 0
    static void deleteProduct(Connection con) throws SQLException {

        String sql = "DELETE FROM Product " +
                "WHERE product_id = ? AND quantity = 0";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.print("Enter Product ID: ");
            int id = sc.nextInt();

            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            System.out.println(rows > 0 ?
                    "Product Deleted Successfully!" :
                    "Cannot Delete: Product not found or quantity is not zero!");
        }
    }
}

