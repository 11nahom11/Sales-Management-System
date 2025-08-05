import java.util.List;

public interface ISaleDAO {
    // Add a new sale and update product stock
    boolean addSale(Sale sale);

    // Get sale by ID
    Sale getSaleById(int saleId);

    // Get all sales
    List<Sale> getAllSales();

    // Update sale and adjust stock if needed
    boolean updateSale(Sale sale);

    // Delete sale and return quantity to stock
    boolean deleteSale(int saleId);
}
