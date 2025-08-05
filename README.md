# Sales-Management-System
Object Oriented Programming assignment
ROLES AND USES OF COMONENTES USED ON THE SALES MANAGEMENT SYSTEM

1. Java Swing (GUI Framework)
•	Use: Java Swing is a graphical user interface (GUI) toolkit for Java. It provides a rich set of pre-built components (like windows, buttons, text fields, tables, combo boxes, tabbed panes) that allow you to create interactive desktop applications.
•	Role in the System:
o	User Interface: It forms the entire visual layer that the user interacts with.
o	Input/Output: It's responsible for capturing user input (e.g., product name, quantity) and displaying data retrieved from the database in a user-friendly format (e.g., tables).
o	Event Handling: It processes user actions (like button clicks, row selections) and translates them into calls to the underlying business logic.
o	Main Class: The SalesManagementSystem.java class is the primary Swing application class, orchestrating the layout and behavior of all GUI elements.
2. PostgreSQL (Relational Database Management System - RDBMS)
•	Use: PostgreSQL is a powerful, open-source object-relational database system. It's used to store, manage, and retrieve structured data reliably and persistently.
•	Role in the System:
o	Data Storage: It's the backend where all your application's data (products, customers, sales) is permanently stored.
o	Data Integrity: It enforces data integrity rules through features like primary keys (ensuring unique records), foreign keys (maintaining relationships between tables), and constraints (e.g., NOT NULL, UNIQUE).
o	Concurrency Control: It manages simultaneous access from multiple users (though in a single-user desktop app, this is less critical, it's a core database feature).
o	Reliability: It ensures data is not lost due to system failures through its robust transaction management and recovery mechanisms.
3. JDBC (Java Database Connectivity)
•	Use: JDBC is a Java API (Application Programming Interface) that defines how a Java application can connect to and interact with a database. It provides a standard way to execute SQL statements and retrieve results.
•	Role in the System:
o	Bridge: It acts as the bridge between your Java application and the PostgreSQL database.
o	Standardization: It provides a common set of interfaces for database operations, meaning your Java code doesn't need to change significantly if you switch from PostgreSQL to MySQL or Oracle (as long as you have the correct JDBC driver).
o	SQL Execution: It allows the DatabaseManager to send SQL commands (like INSERT, SELECT, UPDATE, DELETE) to PostgreSQL and process the responses.
o	PreparedStatement: A key component of JDBC used for executing pre-compiled SQL statements, enhancing performance and, crucially, preventing SQL injection vulnerabilities.
4. POJO Classes (Product.java, Customer.java, Sale.java)
•	Use: POJO stands for Plain Old Java Object. These are simple classes that encapsulate data for a specific entity without containing complex business logic or framework-specific dependencies.
•	Role in the System:
o	Data Modeling: They serve as clear, type-safe models for the real-world entities in your system (a Product has a name, price, stock; a Customer has firstName, lastName, etc.).
o	Data Transfer Objects (DTOs): They are used to transfer data between different layers of the application (e.g., from the GUI to the DatabaseManager, or from the DatabaseManager back to the GUI). Instead of passing around generic String[] arrays, you pass strongly-typed Product or Customer objects, which improves code readability and reduces errors.
o	Encapsulation: They demonstrate encapsulation by keeping their data fields private and providing public getter and setter methods to control access to that data.
5. DAO Interfaces (IProductDAO.java, ICustomerDAO.java, ISaleDAO.java)
•	Use: DAO stands for Data Access Object. The DAO pattern separates the low-level data accessing operations from the high-level business logic. Interfaces define the contract for these operations.
•	Role in the System:
o	Abstraction: They provide an abstract layer over the database operations. The GUI and any future business logic layers interact with these interfaces, not directly with the concrete DatabaseManager class.
o	Loose Coupling: This creates loose coupling between your application's logic and the specific database implementation. If you ever need to switch databases (e.g., from PostgreSQL to SQLite), you would only need to create a new implementation of these DAO interfaces, without modifying the SalesManagementSystem GUI or POJOs.
o	Testability: They make the code more testable. You can create mock implementations of these interfaces for unit testing without needing a live database connection.
o	Contract Definition: They clearly define what database operations are available for each entity (e.g., addProduct, deleteCustomer, updateSale).
6. DatabaseManager.java Class
•	Use: This is the concrete implementation of all the DAO interfaces (IProductDAO, ICustomerDAO, ISaleDAO). It contains the actual JDBC code to perform database operations.
•	Role in the System:
o	Persistence Logic: It encapsulates all the low-level details of interacting with the PostgreSQL database (connecting, executing SQL, handling ResultSets).
o	Table Management: It's responsible for creating the necessary database tables (products, customers, sales) if they don't already exist when the application starts.
o	CRUD Operations: It translates the method calls from the DAO interfaces (e.g., addProduct(Product product)) into specific SQL statements and executes them against the database.
o	Transaction Management: Crucially, for complex operations like adding or updating a sale (which also involves updating product stock), it manages database transactions. This ensures that multiple related database operations are treated as a single, atomic unit. If any part fails, the entire transaction is rolled back, preventing data inconsistencies.
o	Error Handling: It contains the try-catch blocks that specifically handle SQLExceptions arising from database interactions, printing error messages and returning appropriate boolean flags.
7. SalesManagementSystem.java Class
•	Use: This is the main entry point of your application. It initializes the GUI and acts as the orchestrator, connecting the user interface to the data access layer.
•	Role in the System:
o	Application Startup: The main method within this class is where the application execution begins, creating the main Swing window.
o	GUI Construction: It builds and lays out all the Swing components (tabs, fields, buttons, tables).
o	User Interaction Logic: It contains the ActionListener implementations for buttons and ListSelectionListener for tables, defining what happens when a user interacts with the GUI.
o	Data Presentation: It fetches data from the DatabaseManager (via its implemented DAO methods) and populates the JTables and JComboBoxes for display.
o	Input Validation & Feedback: It performs basic input validation before sending data to the DatabaseManager and provides user feedback through JOptionPane messages (success, error, warning).
o	Orchestration: It coordinates the flow of data and control between the user interface and the underlying database operations, ensuring a seamless user experience.
In essence, the system is designed to be layered and modular. The GUI (SalesManagementSystem) talks to the abstract contracts of the DAOs (interfaces), which are then implemented by the DatabaseManager to talk to the actual PostgreSQL database. The POJOs act as the common language for data exchange across these layers. This structure makes the application robust, maintainable, and easier to extend.

