import java.util.List;

public interface IProductDAO {
    // Add a new product
    boolean addProduct(Product product);

    // Get product by ID
    Product getProductById(int productId);

    // Get all products
    List<Product> getAllProducts();

    // Update product details
    boolean updateProduct(Product product);

    // Delete product by ID
    boolean deleteProduct(int productId);

    // Update product stock quantity
    boolean updateProductStock(int productId, int quantityChange);
}
