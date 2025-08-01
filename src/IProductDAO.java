import java.util.List;

/**
 * Interface for Product Data Access Object (DAO) operations.
 * Defines the contract for interacting with the 'products' table.
 */
public interface IProductDAO {
    /**
     * Adds a new product to the database.
     * @param product The Product object to add.
     * @return true if the product was added successfully, false otherwise.
     */
    boolean addProduct(Product product);

    /**
     * Retrieves a product by its ID.
     * @param productId The ID of the product to retrieve.
     * @return The Product object if found, null otherwise.
     */
    Product getProductById(int productId);

    /**
     * Retrieves all products from the database.
     * @return A List of Product objects.
     */
    List<Product> getAllProducts();

    /**
     * Updates an existing product record in the database.
     * @param product The Product object with updated details.
     * @return true if the product was updated successfully, false otherwise.
     */
    boolean updateProduct(Product product);

    /**
     * Deletes a product record from the database based on its ID.
     * @param productId The ID of the product record to delete.
     * @return true if the product was deleted successfully, false otherwise.
     */
    boolean deleteProduct(int productId);

    /**
     * Updates the stock quantity of a product.
     * @param productId The ID of the product to update.
     * @param quantityChange The amount to change the stock by (positive for increase, negative for decrease).
     * @return true if the stock was updated successfully, false otherwise.
     */
    boolean updateProductStock(int productId, int quantityChange);
}
