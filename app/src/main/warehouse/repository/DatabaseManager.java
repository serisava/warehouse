package warehouse.repository;

import warehouse.model.Product;
import warehouse.model.Rack;
import warehouse.model.RackType;
import warehouse.model.StorageZone;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:h2:./warehouse;DB_CLOSE_DELAY=-1";
    //private static final String DB_URL = "jdbc:h2:tcp://localhost/~/warehouse";
    private static final String USER = "sa";
    private static final String PASS = "";

    public DatabaseManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            // Создание таблиц
            stmt.execute("CREATE TABLE IF NOT EXISTS StorageZone (id VARCHAR(50) PRIMARY KEY, name VARCHAR(100))");
            stmt.execute("CREATE TABLE IF NOT EXISTS Rack (id VARCHAR(50) PRIMARY KEY, type VARCHAR(20), zone_id VARCHAR(50), FOREIGN KEY (zone_id) REFERENCES StorageZone(id))");
            stmt.execute("CREATE TABLE IF NOT EXISTS Product (id VARCHAR(50) PRIMARY KEY, name VARCHAR(100), width DOUBLE, height DOUBLE, depth DOUBLE, rack_id VARCHAR(50), FOREIGN KEY (rack_id) REFERENCES Rack(id))");

            // Вставка статических данных о зонах и стеллажах
            stmt.execute("INSERT INTO StorageZone (id, name) VALUES ('zone1', 'Zone A')");
            stmt.execute("INSERT INTO StorageZone (id, name) VALUES ('zone2', 'Zone B')");
            stmt.execute("INSERT INTO Rack (id, type, zone_id) VALUES ('rack1', 'SMALL', 'zone1')");
            stmt.execute("INSERT INTO Rack (id, type, zone_id) VALUES ('rack2', 'MEDIUM', 'zone1')");
            stmt.execute("INSERT INTO Rack (id, type, zone_id) VALUES ('rack3', 'LARGE', 'zone1')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addProduct(Product product) throws SQLException {
        String sql = "INSERT INTO Product (id, name, width, height, depth, rack_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, product.getId());
            pstmt.setString(2, product.getName());
            pstmt.setDouble(3, product.getWidth());
            pstmt.setDouble(4, product.getHeight());
            pstmt.setDouble(5, product.getDepth());
            pstmt.setString(6, product.getRackId());
            pstmt.executeUpdate();
        }
    }

    public void updateProduct(Product product) throws SQLException {
        String sql = "UPDATE Product SET name = ?, width = ?, height = ?, depth = ?, rack_id = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, product.getName());
            pstmt.setDouble(2, product.getWidth());
            pstmt.setDouble(3, product.getHeight());
            pstmt.setDouble(4, product.getDepth());
            pstmt.setString(5, product.getRackId());
            pstmt.setString(6, product.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteProduct(String productId) throws SQLException {
        String sql = "DELETE FROM Product WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, productId);
            pstmt.executeUpdate();
        }
    }

    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Product";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                products.add(new Product(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getDouble("width"),
                        rs.getDouble("height"),
                        rs.getDouble("depth"),
                        rs.getString("rack_id")
                ));
            }
        }
        return products;
    }

    public List<Rack> getAllRacks() throws SQLException {
        List<Rack> racks = new ArrayList<>();
        String sql = "SELECT * FROM Rack";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                racks.add(new Rack(
                        rs.getString("id"),
                        RackType.valueOf(rs.getString("type")),
                        rs.getString("zone_id")
                ));
            }
        }
        return racks;
    }

    public List<StorageZone> getAllZones() throws SQLException {
        List<StorageZone> zones = new ArrayList<>();
        String sql = "SELECT * FROM StorageZone";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                zones.add(new StorageZone(
                        rs.getString("id"),
                        rs.getString("name")
                ));
            }
        }
        return zones;
    }

    public List<Product> searchProductsByName(String name) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Product WHERE name LIKE ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                products.add(new Product(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getDouble("width"),
                        rs.getDouble("height"),
                        rs.getDouble("depth"),
                        rs.getString("rack_id")
                ));
            }
        }
        return products;
    }
}
