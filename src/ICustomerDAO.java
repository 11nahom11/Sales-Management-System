import java.util.List;

public interface ICustomerDAO {
    // Add a new customer
    boolean addCustomer(Customer customer);

    // Get customer by ID
    Customer getCustomerById(int customerId);

    // Get all customers
    List<Customer> getAllCustomers();

    // Update customer details
    boolean updateCustomer(Customer customer);

    // Delete customer by ID
    boolean deleteCustomer(int customerId);

    // Get customer by first and last name
    Customer getCustomerByName(String firstName, String lastName);
}
