package DBTest;

import org.example.models.Product;
import org.example.services.ProductService;
import org.example.utils.DBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceTest {
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        productService = new ProductService();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        clearDatabase();
    }

    private void clearDatabase() throws SQLException {
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("DELETE FROM products");
            stmt.execute("DELETE FROM product_groups");
            stmt.execute("DELETE FROM sqlite_sequence WHERE name='products'"); // Reset AUTOINCREMENT
            stmt.execute("DELETE FROM sqlite_sequence WHERE name='product_groups'"); // Reset AUTOINCREMENT
        }
    }

    private void createProductGroup(String name, String description) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute(String.format("INSERT INTO product_groups (name, description) VALUES ('%s', '%s')", name, description));
        }
    }

    @Test
    public void testCreateAndReadProduct() throws SQLException {
        createProductGroup("Groceries", "Daily grocery items");
        Product product = new Product(0, "Test Product", "Test Description", "Test Producer", 100, 99.99, 1);
        productService.createProduct(product);

        List<Product> products = productService.listProducts("name", "Test Product");
        assertFalse(products.isEmpty());
        assertEquals("Test Product", products.get(0).getName());
    }

    @Test
    public void testUpdateProduct() throws SQLException {
        createProductGroup("Groceries", "Daily grocery items");
        Product product = new Product(0, "Test Product", "Test Description", "Test Producer", 100, 99.99, 1);
        productService.createProduct(product);

        List<Product> products = productService.listProducts("name", "Test Product");
        Product productToUpdate = products.get(0);
        productToUpdate.setName("Updated Product");
        productService.updateProduct(productToUpdate);

        List<Product> updatedProducts = productService.listProducts("name", "Updated Product");
        assertFalse(updatedProducts.isEmpty());
        assertEquals("Updated Product", updatedProducts.get(0).getName());
    }

    @Test
    public void testDeleteProduct() throws SQLException {
        createProductGroup("Groceries", "Daily grocery items");
        Product product = new Product(0, "Test Product", "Test Description", "Test Producer", 100, 99.99, 1);
        productService.createProduct(product);

        List<Product> products = productService.listProducts("name", "Test Product");
        Product productToDelete = products.get(0);
        productService.deleteProduct(productToDelete.getId());

        List<Product> deletedProducts = productService.listProducts("name", "Test Product");
        assertTrue(deletedProducts.isEmpty());
    }

    @Test
    public void testListProductsByCriteria() throws SQLException {
        createProductGroup("Groceries", "Daily grocery items");
        createProductGroup("Beverages", "Drinks and beverages");

        productService.createProduct(new Product(0, "Product One", "Description One", "Producer One", 100, 50.0, 1));
        productService.createProduct(new Product(0, "Product Two", "Description Two", "Producer Two", 200, 75.0, 2));

        List<Product> productsByName = productService.listProducts("name", "Product");
        assertEquals(2, productsByName.size());

        List<Product> productsByProducer = productService.listProducts("producer", "Producer One");
        assertEquals(1, productsByProducer.size());
        assertEquals("Product One", productsByProducer.get(0).getName());
    }
}