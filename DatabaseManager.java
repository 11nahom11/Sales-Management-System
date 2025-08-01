import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date; // For LocalDate conversion
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all database operations for the Sales Management System.
 * This class connects to a PostgreSQL database and performs CRUD operations
 * on 'products', 'customers', and 'sales' tables.
 * It implements IProductDAO, ICustomerDAO, and ISaleDAO for better modularity.
 */
public class DatabaseManager implements IProductDAO, ICustomerDAO, ISaleDAO {

    // Database connection details
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/sales_management_db";
    private static final String DB_USER = "postgres"; // Your PostgreSQL username
    private static final String DB_PASSWORD = "123"; // Your PostgreSQL password

    private Connection connection;

    /**
     * Constructor for DatabaseManager.
     * Attempts to establish a connection to the database and create the necessary tables.
     */
    public DatabaseManager() {
        try {
            // Register the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to the PostgreSQL database successfully!");
            createTables(); // Ensure all necessary tables exist
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found. Make sure it's in your classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database or create tables.");
            e.printStackTrace();
        }
    }

    /**
     * Establishes and returns a database connection.
     * @return A Connection object to the database.
     * @throws SQLException if a database access error occurs.
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
        return connection;
    }

    /**
     * Creates the 'products', 'customers', and 'sales' tables in the database if they do not already exist.
     */
    private void createTables() {
        // Create products table
        String createProductsTableSQL = "CREATE TABLE IF NOT EXISTS products (" +
                "product_id SERIAL PRIMARY KEY," +
                "name VARCHAR(255) UNIQUE NOT NULL," +
                "price DECIMAL(10, 2) NOT NULL," +
                "stock INT NOT NULL" +
                ");";

        // Create customers table
        String createCustomersTableSQL = "CREATE TABLE IF NOT EXISTS customers (" +
                "customer_id SERIAL PRIMARY KEY," +
                "first_name VARCHAR(100) NOT NULL," +
                "last_name VARCHAR(100) NOT NULL," +
                "email VARCHAR(100) UNIQUE," +
                "phone VARCHAR(20)" +
                ");";

        // Create sales table (with foreign keys to products and customers)
        String createSalesTableSQL = "CREATE TABLE IF NOT EXISTS sales (" +
                "sale_id SERIAL PRIMARY KEY," +
                "product_id INT NOT NULL," +
                "customer_id INT NOT NULL," +
                "quantity INT NOT NULL," +
                "unit_price_at_sale DECIMAL(10, 2) NOT NULL," +
                "total_sale_price DECIMAL(10, 2) NOT NULL," +
                "sale_date DATE NOT NULL," +
                "FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT," + // Prevent deleting product if sales exist
                "FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE RESTRICT" + // Prevent deleting customer if sales exist
                ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createProductsTableSQL);
            stmt.execute(createCustomersTableSQL);
            stmt.execute(createSalesTableSQL);
            System.out.println("All tables (products, customers, sales) checked/created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Product DAO Implementation ---

    @Override
    public boolean addProduct(Product product) {
        String insertSQL = "INSERT INTO products (name, price, stock) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, product.getName());
            pstmt.setDouble(2, product.getPrice());
            pstmt.setInt(3, product.getStock());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Product getProductById(int productId) {
        String selectSQL = "SELECT product_id, name, price, stock FROM products WHERE product_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting product by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String selectSQL = "SELECT product_id, name, price, stock FROM products ORDER BY product_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving products: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public boolean updateProduct(Product product) {
        String updateSQL = "UPDATE products SET name = ?, price = ?, stock = ? WHERE product_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            pstmt.setString(1, product.getName());
            pstmt.setDouble(2, product.getPrice());
            pstmt.setInt(3, product.getStock());
            pstmt.setInt(4, product.getProductId());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteProduct(int productId) {
        String deleteSQL = "DELETE FROM products WHERE product_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, productId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateProductStock(int productId, int quantityChange) {
        String updateSQL = "UPDATE products SET stock = stock + ? WHERE product_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            pstmt.setInt(1, quantityChange);
            pstmt.setInt(2, productId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating product stock: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // --- Customer DAO Implementation ---

    @Override
    public boolean addCustomer(Customer customer) {
        String insertSQL = "INSERT INTO customers (first_name, last_name, email, phone) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getPhone());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Customer getCustomerById(int customerId) {
        String selectSQL = "SELECT customer_id, first_name, last_name, email, phone FROM customers WHERE customer_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String selectSQL = "SELECT customer_id, first_name, last_name, email, phone FROM customers ORDER BY customer_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            while (rs.next()) {
                customers.add(new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving customers: " + e.getMessage());
            e.printStackTrace();
        }
        return customers;
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        String updateSQL = "UPDATE customers SET first_name = ?, last_name = ?, email = ?, phone = ? WHERE customer_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getPhone());
            pstmt.setInt(5, customer.getCustomerId());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteCustomer(int customerId) {
        String deleteSQL = "DELETE FROM customers WHERE customer_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, customerId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Customer getCustomerByName(String firstName, String lastName) {
        String selectSQL = "SELECT customer_id, first_name, last_name, email, phone FROM customers WHERE first_name = ? AND last_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer by name: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // --- Sale DAO Implementation ---

    @Override
    public boolean addSale(Sale sale) {
        // Start a transaction for atomicity (sale + stock update)
        try {
            connection.setAutoCommit(false); // Disable auto-commit

            // 1. Check product stock
            Product product = getProductById(sale.getProductId());
            if (product == null || product.getStock() < sale.getQuantity()) {
                System.err.println("Insufficient stock for product ID: " + sale.getProductId());
                connection.rollback(); // Rollback transaction
                return false;
            }

            // 2. Insert sale record
            String insertSQL = "INSERT INTO sales (product_id, customer_id, quantity, unit_price_at_sale, total_sale_price, sale_date) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                pstmt.setInt(1, sale.getProductId());
                pstmt.setInt(2, sale.getCustomerId());
                pstmt.setInt(3, sale.getQuantity());
                pstmt.setDouble(4, sale.getUnitPriceAtSale());
                pstmt.setDouble(5, sale.getTotalSalePrice());
                pstmt.setDate(6, Date.valueOf(sale.getSaleDate()));
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected == 0) {
                    connection.rollback(); // Rollback if sale insert fails
                    return false;
                }
            }

            // 3. Update product stock (decrease)
            if (!updateProductStock(sale.getProductId(), -sale.getQuantity())) {
                connection.rollback(); // Rollback if stock update fails
                return false;
            }

            connection.commit(); // Commit transaction if all successful
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding sale (transaction rolled back): " + e.getMessage());
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Error during rollback: " + ex.getMessage());
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (connection != null) connection.setAutoCommit(true); // Re-enable auto-commit
            } catch (SQLException e) {
                System.err.println("Error re-enabling auto-commit: " + e.getMessage());
            }
        }
    }

    @Override
    public Sale getSaleById(int saleId) {
        String selectSQL = "SELECT sale_id, product_id, customer_id, quantity, unit_price_at_sale, total_sale_price, sale_date FROM sales WHERE sale_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setInt(1, saleId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Sale(
                        rs.getInt("sale_id"),
                        rs.getInt("product_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price_at_sale"),
                        rs.getDouble("total_sale_price"),
                        rs.getDate("sale_date").toLocalDate()
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting sale by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Sale> getAllSales() {
        List<Sale> sales = new ArrayList<>();
        String selectSQL = "SELECT sale_id, product_id, customer_id, quantity, unit_price_at_sale, total_sale_price, sale_date FROM sales ORDER BY sale_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            while (rs.next()) {
                sales.add(new Sale(
                        rs.getInt("sale_id"),
                        rs.getInt("product_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price_at_sale"),
                        rs.getDouble("total_sale_price"),
                        rs.getDate("sale_date").toLocalDate()
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving sales: " + e.getMessage());
            e.printStackTrace();
        }
        return sales;
    }

    @Override
    public boolean updateSale(Sale sale) {
        // Start a transaction for atomicity (sale update + stock adjustment)
        try {
            connection.setAutoCommit(false); // Disable auto-commit

            // 1. Get old sale details to calculate stock difference
            Sale oldSale = getSaleById(sale.getSaleId());
            if (oldSale == null) {
                System.err.println("Sale with ID " + sale.getSaleId() + " not found for update.");
                connection.rollback();
                return false;
            }

            int oldQuantity = oldSale.getQuantity();
            int newQuantity = sale.getQuantity();
            int quantityDifference = newQuantity - oldQuantity; // Positive if quantity increased, negative if decreased

            // 2. Check product stock if quantity increased
            if (quantityDifference > 0) {
                Product product = getProductById(sale.getProductId());
                if (product == null || product.getStock() < quantityDifference) {
                    System.err.println("Insufficient stock to increase quantity for product ID: " + sale.getProductId());
                    connection.rollback();
                    return false;
                }
            }

            // 3. Update sale record
            String updateSQL = "UPDATE sales SET product_id = ?, customer_id = ?, quantity = ?, unit_price_at_sale = ?, total_sale_price = ?, sale_date = ? WHERE sale_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
                pstmt.setInt(1, sale.getProductId());
                pstmt.setInt(2, sale.getCustomerId());
                pstmt.setInt(3, sale.getQuantity());
                pstmt.setDouble(4, sale.getUnitPriceAtSale());
                pstmt.setDouble(5, sale.getTotalSalePrice());
                pstmt.setDate(6, Date.valueOf(sale.getSaleDate()));
                pstmt.setInt(7, sale.getSaleId());
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected == 0) {
                    connection.rollback();
                    return false;
                }
            }

            // 4. Adjust product stock
            if (quantityDifference != 0) { // Only update stock if quantity changed
                if (!updateProductStock(sale.getProductId(), -quantityDifference)) { // Decrease stock by difference
                    connection.rollback();
                    return false;
                }
            }

            connection.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating sale (transaction rolled back): " + e.getMessage());
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Error during rollback: " + ex.getMessage());
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (connection != null) connection.setAutoCommit(true); // Re-enable auto-commit
            } catch (SQLException e) {
                System.err.println("Error re-enabling auto-commit: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean deleteSale(int saleId) {
        // Start a transaction for atomicity (sale deletion + stock return)
        try {
            connection.setAutoCommit(false); // Disable auto-commit

            // 1. Get sale details to return stock
            Sale saleToDelete = getSaleById(saleId);
            if (saleToDelete == null) {
                System.err.println("Sale with ID " + saleId + " not found for deletion.");
                connection.rollback();
                return false;
            }

            // 2. Delete sale record
            String deleteSQL = "DELETE FROM sales WHERE sale_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
                pstmt.setInt(1, saleId);
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected == 0) {
                    connection.rollback();
                    return false;
                }
            }

            // 3. Return product quantity to stock
            if (!updateProductStock(saleToDelete.getProductId(), saleToDelete.getQuantity())) {
                connection.rollback(); // Rollback if stock update fails
                return false;
            }

            connection.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting sale (transaction rolled back): " + e.getMessage());
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Error during rollback: " + ex.getMessage());
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (connection != null) connection.setAutoCommit(true); // Re-enable auto-commit
            } catch (SQLException e) {
                System.err.println("Error re-enabling auto-commit: " + e.getMessage());
            }
        }
    }

    /**
     * Closes the database connection.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
