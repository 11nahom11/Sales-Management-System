import java.util.List;

/**
 * Interface for Sale Data Access Object (DAO) operations.
 * Defines the contract for interacting with the 'sales' table.
 */
public interface ISaleDAO {
    /**
     * Adds a new sale record to the database.
     * This method should also handle updating product stock.
     * @param sale The Sale object to add.
     * @return true if the sale was added successfully, false otherwise.
     */
    boolean addSale(Sale sale);

    /**
     * Retrieves a sale by its ID.
     * @param saleId The ID of the sale to retrieve.
     * @return The Sale object if found, null otherwise.
     */
    Sale getSaleById(int saleId);

    /**
     * Retrieves all sales records from the database.
     * @return A List of Sale objects.
     */
    List<Sale> getAllSales();

    /**
     * Updates an existing sale record in the database.
     * This method should also handle adjusting product stock if quantity changes.
     * @param sale The Sale object with updated details.
     * @return true if the sale was updated successfully, false otherwise.
     */
    boolean updateSale(Sale sale);

    /**
     * Deletes a sale record from the database based on its ID.
     * This method should also handle returning the product quantity to stock.
     * @param saleId The ID of the sale record to delete.
     * @return true if the sale was deleted successfully, false otherwise.
     */
    boolean deleteSale(int saleId);
}
