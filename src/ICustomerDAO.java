import java.util.List;

/**
 * Interface for Customer Data Access Object (DAO) operations.
 * Defines the contract for interacting with the 'customers' table.
 */
public interface ICustomerDAO {
    /**
     * Adds a new customer to the database.
     * @param customer The Customer object to add.
     * @return true if the customer was added successfully, false otherwise.
     */
    boolean addCustomer(Customer customer);

    /**
     * Retrieves a customer by their ID.
     * @param customerId The ID of the customer to retrieve.
     * @return The Customer object if found, null otherwise.
     */
    Customer getCustomerById(int customerId);

    /**
     * Retrieves all customers from the database.
     * @return A List of Customer objects.
     */
    List<Customer> getAllCustomers();

    /**
     * Updates an existing customer record in the database.
     * @param customer The Customer object with updated details.
     * @return true if the customer was updated successfully, false otherwise.
     */
    boolean updateCustomer(Customer customer);

    /**
     * Deletes a customer record from the database based on their ID.
     * @param customerId The ID of the customer record to delete.
     * @return true if the customer was deleted successfully, false otherwise.
     */
    boolean deleteCustomer(int customerId);

    /**
     * Retrieves a customer by their first name and last name.
     * @param firstName The first name of the customer.
     * @param lastName The last name of the customer.
     * @return The Customer object if found, null otherwise.
     */
    Customer getCustomerByName(String firstName, String lastName);
}
