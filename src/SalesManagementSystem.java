import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

/**
 * SalesManagementSystem is a Java Swing application for managing sales, products, and customers.
 * It interacts with a PostgreSQL database via the DatabaseManager class, using POJO and DAO patterns.
 */
public class SalesManagementSystem extends JFrame {

    private DatabaseManager dbManager; // Instance of our database manager

    // --- GUI Components ---
    private JTabbedPane tabbedPane;

    // Product Tab Components
    private JTextField productIdField, productNameField, productPriceField, productStockField;
    private JButton addProductButton, updateProductButton, deleteProductButton;
    private JTable productsTable;
    private DefaultTableModel productsTableModel;

    // Customer Tab Components
    private JTextField customerIdField, customerFirstNameField, customerLastNameField, customerEmailField, customerPhoneField;
    private JButton addCustomerButton, updateCustomerButton, deleteCustomerButton;
    private JTable customersTable;
    private DefaultTableModel customersTableModel;

    // Sale Tab Components
    private JTextField saleIdField, saleQuantityField, saleUnitPriceField, saleDate;
    private JComboBox<String> saleProductComboBox, saleCustomerComboBox;
    private JButton addSaleButton, updateSaleButton, deleteSaleButton;
    private JTable salesTable;
    private DefaultTableModel salesTableModel;

    // Maps to store ID-to-Name for ComboBoxes
    private Map<String, Integer> productNameToIdMap;
    private Map<Integer, String> productIdToNameMap;
    private Map<String, Integer> customerNameToIdMap;
    private Map<Integer, String> customerIdToNameMap;


    /**
     * Constructor for the SalesManagementSystem GUI.
     * Initializes the database manager and sets up the Swing components.
     */
    public SalesManagementSystem() {
        super("Advanced Sales Management System"); // Set window title

        dbManager = new DatabaseManager(); // Initialize the database manager
        productNameToIdMap = new HashMap<>();
        productIdToNameMap = new HashMap<>();
        customerNameToIdMap = new HashMap<>();
        customerIdToNameMap = new HashMap<>();

        // Set up the main frame
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window on the screen
        setLayout(new BorderLayout()); // Use BorderLayout for the main frame

        tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        // --- Setup Tabs ---
        setupProductTab();
        setupCustomerTab();
        setupSaleTab();

        // Load initial data for all tables and combo boxes
        loadProductsData();
        loadCustomersData();
        loadSalesData();

        // Make the frame visible
        setVisible(true);
    }

    /**
     * Sets up the Product Management tab.
     */
    private void setupProductTab() {
        JPanel productPanel = new JPanel(new BorderLayout(10, 10));
        productPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input Panel for Products
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Product Details"));

        productIdField = new JTextField(10);
        productIdField.setEditable(false); // ID is auto-generated or selected from table
        productNameField = new JTextField(20);
        productPriceField = new JTextField(10);
        productStockField = new JTextField(10);

        inputPanel.add(new JLabel("Product ID (Auto/Select):"));
        inputPanel.add(productIdField);
        inputPanel.add(new JLabel("Product Name:"));
        inputPanel.add(productNameField);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(productPriceField);
        inputPanel.add(new JLabel("Stock:"));
        inputPanel.add(productStockField);

        productPanel.add(inputPanel, BorderLayout.NORTH);

        // Button Panel for Products
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addProductButton = new JButton("Add Product");
        updateProductButton = new JButton("Update Product");
        deleteProductButton = new JButton("Delete Product");

        buttonPanel.add(addProductButton);
        buttonPanel.add(updateProductButton);
        buttonPanel.add(deleteProductButton);

        productPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Table for Products
        String[] productColumnNames = {"ID", "Name", "Price", "Stock"};
        productsTableModel = new DefaultTableModel(productColumnNames, 0);
        productsTable = new JTable(productsTableModel);
        productsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productsTable.getTableHeader().setReorderingAllowed(false);

        productsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && productsTable.getSelectedRow() != -1) {
                int selectedRow = productsTable.getSelectedRow();
                productIdField.setText(productsTableModel.getValueAt(selectedRow, 0).toString());
                productNameField.setText(productsTableModel.getValueAt(selectedRow, 1).toString());
                productPriceField.setText(productsTableModel.getValueAt(selectedRow, 2).toString());
                productStockField.setText(productsTableModel.getValueAt(selectedRow, 3).toString());
            }
        });

        JScrollPane productScrollPane = new JScrollPane(productsTable);
        productPanel.add(productScrollPane, BorderLayout.CENTER);

        // Add action listeners
        addProductButton.addActionListener(e -> addProduct());
        updateProductButton.addActionListener(e -> updateProduct());
        deleteProductButton.addActionListener(e -> deleteProduct());

        tabbedPane.addTab("Product Management", productPanel);
    }

    /**
     * Sets up the Customer Management tab.
     */
    private void setupCustomerTab() {
        JPanel customerPanel = new JPanel(new BorderLayout(10, 10));
        customerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input Panel for Customers
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));

        customerIdField = new JTextField(10);
        customerIdField.setEditable(false);
        customerFirstNameField = new JTextField(20);
        customerLastNameField = new JTextField(20);
        customerEmailField = new JTextField(20);
        customerPhoneField = new JTextField(20);

        inputPanel.add(new JLabel("Customer ID (Auto/Select):"));
        inputPanel.add(customerIdField);
        inputPanel.add(new JLabel("First Name:"));
        inputPanel.add(customerFirstNameField);
        inputPanel.add(new JLabel("Last Name:"));
        inputPanel.add(customerLastNameField);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(customerEmailField);
        inputPanel.add(new JLabel("Phone:"));
        inputPanel.add(customerPhoneField);

        customerPanel.add(inputPanel, BorderLayout.NORTH);

        // Button Panel for Customers
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addCustomerButton = new JButton("Add Customer");
        updateCustomerButton = new JButton("Update Customer");
        deleteCustomerButton = new JButton("Delete Customer");

        buttonPanel.add(addCustomerButton);
        buttonPanel.add(updateCustomerButton);
        buttonPanel.add(deleteCustomerButton);

        customerPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Table for Customers
        String[] customerColumnNames = {"ID", "First Name", "Last Name", "Email", "Phone"};
        customersTableModel = new DefaultTableModel(customerColumnNames, 0);
        customersTable = new JTable(customersTableModel);
        customersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customersTable.getTableHeader().setReorderingAllowed(false);

        customersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && customersTable.getSelectedRow() != -1) {
                int selectedRow = customersTable.getSelectedRow();
                customerIdField.setText(customersTableModel.getValueAt(selectedRow, 0).toString());
                customerFirstNameField.setText(customersTableModel.getValueAt(selectedRow, 1).toString());
                customerLastNameField.setText(customersTableModel.getValueAt(selectedRow, 2).toString());
                customerEmailField.setText(customersTableModel.getValueAt(selectedRow, 3).toString());
                customerPhoneField.setText(customersTableModel.getValueAt(selectedRow, 4).toString());
            }
        });

        JScrollPane customerScrollPane = new JScrollPane(customersTable);
        customerPanel.add(customerScrollPane, BorderLayout.CENTER);

        // Add action listeners
        addCustomerButton.addActionListener(e -> addCustomer());
        updateCustomerButton.addActionListener(e -> updateCustomer());
        deleteCustomerButton.addActionListener(e -> deleteCustomer());

        tabbedPane.addTab("Customer Management", customerPanel);
    }

    /**
     * Sets up the Sale Management tab.
     */
    private void setupSaleTab() {
        JPanel salePanel = new JPanel(new BorderLayout(10, 10));
        salePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input Panel for Sales
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Sale Details"));

        saleIdField = new JTextField(10);
        saleIdField.setEditable(false);
        saleQuantityField = new JTextField(10);
        saleUnitPriceField = new JTextField(10);
        saleUnitPriceField.setEditable(false); // Unit price comes from selected product
        saleDate = new JTextField(10); // Format: YYYY-MM-DD

        saleProductComboBox = new JComboBox<>(); // Populated dynamically
        saleCustomerComboBox = new JComboBox<>(); // Populated dynamically

        // Listener to update unit price when product selection changes
        saleProductComboBox.addActionListener(e -> {
            String selectedProductName = (String) saleProductComboBox.getSelectedItem();
            if (selectedProductName != null && productNameToIdMap.containsKey(selectedProductName)) {
                int productId = productNameToIdMap.get(selectedProductName);
                Product product = dbManager.getProductById(productId);
                if (product != null) {
                    saleUnitPriceField.setText(String.format("%.2f", product.getPrice()));
                }
            } else {
                saleUnitPriceField.setText("");
            }
        });


        inputPanel.add(new JLabel("Sale ID (Auto/Select):"));
        inputPanel.add(saleIdField);
        inputPanel.add(new JLabel("Product:"));
        inputPanel.add(saleProductComboBox);
        inputPanel.add(new JLabel("Customer:"));
        inputPanel.add(saleCustomerComboBox);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(saleQuantityField);
        inputPanel.add(new JLabel("Unit Price (Auto-filled):"));
        inputPanel.add(saleUnitPriceField);
        inputPanel.add(new JLabel("Sale Date (YYYY-MM-DD):"));
        inputPanel.add(saleDate);

        salePanel.add(inputPanel, BorderLayout.NORTH);

        // Button Panel for Sales
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addSaleButton = new JButton("Add Sale");
        updateSaleButton = new JButton("Update Sale");
        deleteSaleButton = new JButton("Delete Sale");

        buttonPanel.add(addSaleButton);
        buttonPanel.add(updateSaleButton);
        buttonPanel.add(deleteSaleButton);

        salePanel.add(buttonPanel, BorderLayout.SOUTH);

        // Table for Sales
        String[] saleColumnNames = {"ID", "Product", "Customer", "Quantity", "Unit Price", "Total Price", "Sale Date"};
        salesTableModel = new DefaultTableModel(saleColumnNames, 0);
        salesTable = new JTable(salesTableModel);
        salesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        salesTable.getTableHeader().setReorderingAllowed(false);

        salesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && salesTable.getSelectedRow() != -1) {
                int selectedRow = salesTable.getSelectedRow();
                saleIdField.setText(salesTableModel.getValueAt(selectedRow, 0).toString());

                // Set product and customer combo boxes by name
                String productName = salesTableModel.getValueAt(selectedRow, 1).toString();
                saleProductComboBox.setSelectedItem(productName);

                String customerName = salesTableModel.getValueAt(selectedRow, 2).toString();
                saleCustomerComboBox.setSelectedItem(customerName);

                saleQuantityField.setText(salesTableModel.getValueAt(selectedRow, 3).toString());
                saleUnitPriceField.setText(salesTableModel.getValueAt(selectedRow, 4).toString()); // Auto-filled from product selection
                saleDate.setText(salesTableModel.getValueAt(selectedRow, 6).toString());
            }
        });

        JScrollPane saleScrollPane = new JScrollPane(salesTable);
        salePanel.add(saleScrollPane, BorderLayout.CENTER);

        // Add action listeners
        addSaleButton.addActionListener(e -> addSale());
        updateSaleButton.addActionListener(e -> updateSale());
        deleteSaleButton.addActionListener(e -> deleteSale());

        tabbedPane.addTab("Sales Management", salePanel);
    }

    // --- Utility Methods for Clearing Fields and Loading Data ---

    /** Clears input fields for the Product tab. */
    private void clearProductFields() {
        productIdField.setText("");
        productNameField.setText("");
        productPriceField.setText("");
        productStockField.setText("");
        productsTable.clearSelection();
    }

    /** Clears input fields for the Customer tab. */
    private void clearCustomerFields() {
        customerIdField.setText("");
        customerFirstNameField.setText("");
        customerLastNameField.setText("");
        customerEmailField.setText("");
        customerPhoneField.setText("");
        customersTable.clearSelection();
    }

    /** Clears input fields for the Sale tab. */
    private void clearSaleFields() {
        saleIdField.setText("");
        saleProductComboBox.setSelectedIndex(-1); // No selection
        saleCustomerComboBox.setSelectedIndex(-1); // No selection
        saleQuantityField.setText("");
        saleUnitPriceField.setText("");
        saleDate.setText(LocalDate.now().toString()); // Default to today's date
        salesTable.clearSelection();
    }

    /** Loads all product data from the database and populates the products JTable and product combo box. */
    private void loadProductsData() {
        productsTableModel.setRowCount(0); // Clear existing data
        productNameToIdMap.clear();
        productIdToNameMap.clear();

        List<Product> products = dbManager.getAllProducts();
        Vector<String> productNames = new Vector<>();
        for (Product product : products) {
            productsTableModel.addRow(new Object[]{
                    product.getProductId(),
                    product.getName(),
                    String.format("%.2f", product.getPrice()),
                    product.getStock()
            });
            productNames.add(product.getName());
            productNameToIdMap.put(product.getName(), product.getProductId());
            productIdToNameMap.put(product.getProductId(), product.getName());
        }
        saleProductComboBox.setModel(new DefaultComboBoxModel<>(productNames));
        if (!productNames.isEmpty()) {
            saleProductComboBox.setSelectedIndex(0); // Select first item by default
        } else {
            saleProductComboBox.setSelectedIndex(-1); // No selection
        }
    }

    /** Loads all customer data from the database and populates the customers JTable and customer combo box. */
    private void loadCustomersData() {
        customersTableModel.setRowCount(0); // Clear existing data
        customerNameToIdMap.clear();
        customerIdToNameMap.clear();

        List<Customer> customers = dbManager.getAllCustomers();
        Vector<String> customerNames = new Vector<>();
        for (Customer customer : customers) {
            customersTableModel.addRow(new Object[]{
                    customer.getCustomerId(),
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getEmail(),
                    customer.getPhone()
            });
            String fullName = customer.getFirstName() + " " + customer.getLastName();
            customerNames.add(fullName);
            customerNameToIdMap.put(fullName, customer.getCustomerId());
            customerIdToNameMap.put(customer.getCustomerId(), fullName);
        }
        saleCustomerComboBox.setModel(new DefaultComboBoxModel<>(customerNames));
        if (!customerNames.isEmpty()) {
            saleCustomerComboBox.setSelectedIndex(0); // Select first item by default
        } else {
            saleCustomerComboBox.setSelectedIndex(-1); // No selection
        }
    }

    /** Loads all sales data from the database and populates the sales JTable. */
    private void loadSalesData() {
        salesTableModel.setRowCount(0); // Clear existing data
        List<Sale> sales = dbManager.getAllSales();
        for (Sale sale : sales) {
            String productName = productIdToNameMap.getOrDefault(sale.getProductId(), "Unknown Product");
            String customerName = customerIdToNameMap.getOrDefault(sale.getCustomerId(), "Unknown Customer");

            salesTableModel.addRow(new Object[]{
                    sale.getSaleId(),
                    productName,
                    customerName,
                    sale.getQuantity(),
                    String.format("%.2f", sale.getUnitPriceAtSale()),
                    String.format("%.2f", sale.getTotalSalePrice()),
                    sale.getSaleDate().toString()
            });
        }
    }

    // --- Action Methods for Product Tab ---

    private void addProduct() {
        String name = productNameField.getText().trim();
        String priceStr = productPriceField.getText().trim();
        String stockStr = productStockField.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);

            if (price <= 0 || stock < 0) {
                JOptionPane.showMessageDialog(this, "Price must be positive and Stock non-negative.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Product product = new Product(name, price, stock);
            if (dbManager.addProduct(product)) {
                JOptionPane.showMessageDialog(this, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearProductFields();
                loadProductsData(); // Refresh product table and combo boxes
                loadSalesData(); // Refresh sales table as product names might change
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add product. Name might already exist.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price and Stock must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProduct() {
        String idStr = productIdField.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a product from the table to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = productNameField.getText().trim();
        String priceStr = productPriceField.getText().trim();
        String stockStr = productStockField.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required for update.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int productId = Integer.parseInt(idStr);
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);

            if (price <= 0 || stock < 0) {
                JOptionPane.showMessageDialog(this, "Price must be positive and Stock non-negative.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Product product = new Product(productId, name, price, stock);
            if (dbManager.updateProduct(product)) {
                JOptionPane.showMessageDialog(this, "Product updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearProductFields();
                loadProductsData(); // Refresh product table and combo boxes
                loadSalesData(); // Refresh sales table as product details might change
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update product. Name might already exist or ID not found.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID, Price, and Stock must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        String idStr = productIdField.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a product from the table to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int productId = Integer.parseInt(idStr);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this product? This will fail if there are sales associated with it.", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (dbManager.deleteProduct(productId)) {
                    JOptionPane.showMessageDialog(this, "Product deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearProductFields();
                    loadProductsData(); // Refresh product table and combo boxes
                    loadSalesData(); // Refresh sales table as product might be gone
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete product. It might be referenced by existing sales.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Product ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Action Methods for Customer Tab ---

    private void addCustomer() {
        String firstName = customerFirstNameField.getText().trim();
        String lastName = customerLastNameField.getText().trim();
        String email = customerEmailField.getText().trim();
        String phone = customerPhoneField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First Name and Last Name are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Customer customer = new Customer(firstName, lastName, email, phone);
        if (dbManager.addCustomer(customer)) {
            JOptionPane.showMessageDialog(this, "Customer added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearCustomerFields();
            loadCustomersData(); // Refresh customer table and combo boxes
            loadSalesData(); // Refresh sales table as customer names might change
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add customer. Email might already exist.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCustomer() {
        String idStr = customerIdField.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a customer from the table to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String firstName = customerFirstNameField.getText().trim();
        String lastName = customerLastNameField.getText().trim();
        String email = customerEmailField.getText().trim();
        String phone = customerPhoneField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First Name and Last Name are required for update.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int customerId = Integer.parseInt(idStr);
            Customer customer = new Customer(customerId, firstName, lastName, email, phone);
            if (dbManager.updateCustomer(customer)) {
                JOptionPane.showMessageDialog(this, "Customer updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearCustomerFields();
                loadCustomersData(); // Refresh customer table and combo boxes
                loadSalesData(); // Refresh sales table as customer names might change
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update customer. Email might already exist or ID not found.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Customer ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomer() {
        String idStr = customerIdField.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a customer from the table to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int customerId = Integer.parseInt(idStr);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this customer? This will fail if there are sales associated with them.", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (dbManager.deleteCustomer(customerId)) {
                    JOptionPane.showMessageDialog(this, "Customer deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearCustomerFields();
                    loadCustomersData(); // Refresh customer table and combo boxes
                    loadSalesData(); // Refresh sales table as customer might be gone
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete customer. They might be referenced by existing sales.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Customer ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Action Methods for Sale Tab ---

    private void addSale() {
        String selectedProductName = (String) saleProductComboBox.getSelectedItem();
        String selectedCustomerName = (String) saleCustomerComboBox.getSelectedItem();
        String quantityStr = saleQuantityField.getText().trim();
        String unitPriceStr = saleUnitPriceField.getText().trim(); // This comes from product selection
        String saleDateStr = saleDate.getText().trim();

        if (selectedProductName == null || selectedCustomerName == null || quantityStr.isEmpty() || saleDateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Product, Customer, Quantity, and Sale Date are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int productId = productNameToIdMap.get(selectedProductName);
            int customerId = customerNameToIdMap.get(selectedCustomerName);
            int quantity = Integer.parseInt(quantityStr);
            double unitPriceAtSale = Double.parseDouble(unitPriceStr); // Use the auto-filled price
            LocalDate date = LocalDate.parse(saleDateStr);

            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be a positive number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Sale sale = new Sale(productId, customerId, quantity, unitPriceAtSale, date);

            if (dbManager.addSale(sale)) {
                JOptionPane.showMessageDialog(this, "Sale added successfully! Product stock updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearSaleFields();
                loadSalesData(); // Refresh sales table
                loadProductsData(); // Refresh product table (stock changed)
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add sale. Check stock or database connection.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Sale Date format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void updateSale() {
        String idStr = saleIdField.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a sale from the table to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selectedProductName = (String) saleProductComboBox.getSelectedItem();
        String selectedCustomerName = (String) saleCustomerComboBox.getSelectedItem();
        String quantityStr = saleQuantityField.getText().trim();
        String unitPriceStr = saleUnitPriceField.getText().trim();
        String saleDateStr = saleDate.getText().trim();

        if (selectedProductName == null || selectedCustomerName == null || quantityStr.isEmpty() || saleDateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All sale fields are required for update.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int saleId = Integer.parseInt(idStr);
            int productId = productNameToIdMap.get(selectedProductName);
            int customerId = customerNameToIdMap.get(selectedCustomerName);
            int quantity = Integer.parseInt(quantityStr);
            double unitPriceAtSale = Double.parseDouble(unitPriceStr);
            LocalDate date = LocalDate.parse(saleDateStr);

            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be a positive number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Sale sale = new Sale(saleId, productId, customerId, quantity, unitPriceAtSale, unitPriceAtSale * quantity, date);

            if (dbManager.updateSale(sale)) {
                JOptionPane.showMessageDialog(this, "Sale updated successfully! Product stock adjusted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearSaleFields();
                loadSalesData(); // Refresh sales table
                loadProductsData(); // Refresh product table (stock changed)
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update sale. Check stock, ID, or database connection.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID and Quantity must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Sale Date format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteSale() {
        String idStr = saleIdField.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a sale from the table to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int saleId = Integer.parseInt(idStr);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this sale? Product stock will be returned.", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (dbManager.deleteSale(saleId)) {
                    JOptionPane.showMessageDialog(this, "Sale deleted successfully! Product stock returned.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearSaleFields();
                    loadSalesData(); // Refresh sales table
                    loadProductsData(); // Refresh product table (stock changed)
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete sale. Check if ID exists.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Sale ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred during deletion: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    /**
     * Main method to run the Sales Management System application.
     * Ensures the GUI is created and updated on the Event Dispatch Thread (EDT).
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Ensure GUI updates are done on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SalesManagementSystem();
            }
        });
    }
}
