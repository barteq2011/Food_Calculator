package mainPackage.dataOperating;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mainPackage.Product;
import mainPackage.dialogs.Alerts;

import java.sql.*;

public final class ProductData {

    // SQL wildcard user to query for all products
    private static final String ALL_PRODUCTS_VALUE = "_";

    private static final ProductData instance = new ProductData();

    // Constants used to connect to database
    private static final String DB_NAME = "Products.db";
    private static final String CONNECTION_STRING = "jdbc:sqlite:" + ProductData.class.getResource(DB_NAME);

    // Main table and columns
    private static final String TABLE_PRODUCTS = "products";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_KCAL = "kcal";
    private static final String COLUMN_PROTEIN = "protein";
    private static final String COLUMN_CARBO = "carbo";
    private static final String COLUMN_FAT = "fat";
    private static final String COLUMN_IMG_NAME = "imgName";

    // Query used to build products
    private static final String QUERY_PRODUCTS = "SELECT "
            + COLUMN_NAME + ", "
            + COLUMN_KCAL + ", "
            + COLUMN_PROTEIN + ", "
            + COLUMN_CARBO + ", "
            + COLUMN_FAT + ", "
            + COLUMN_IMG_NAME
            + " FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_NAME +
            " LIKE ?";

    private Connection connection;
    private PreparedStatement queryProductsByName;

    public static ProductData getInstance() {
        return instance;
    }

    // Open connection with database and prepare query
    public boolean open() {
        try {
            connection = DriverManager.getConnection(CONNECTION_STRING);
            queryProductsByName = connection.prepareStatement(QUERY_PRODUCTS);
            return true;
        } catch (SQLException e) {
            Alerts.showError("Cannot connect to database");
            return false;
        }
    }

    // Close connection with datbaase and prepared query
    public void close() {
        try {

            if (queryProductsByName != null)
                queryProductsByName.close();
            if (connection != null)
                connection.close();

        } catch (SQLException e) {
            Alerts.showError("Error closing connection with database");
        }
    }

    // Method which query for all products
    public ObservableList<Product> queryProducts() {
        return queryProducts(ALL_PRODUCTS_VALUE);
    }

    // Search for products that name starts with entered phrase
    public ObservableList<Product> queryProducts(String name) {
        if (name != null) {
            try {
                // Concat wildcard to match products names starts with entered phrase
                name = name.concat("%");
                // Set name value in query
                queryProductsByName.setString(1, name);
            } catch (SQLException e) {
                Alerts.showError("Error quering database");
            }
            // Handle query with setted name value
            return handleQuery(queryProductsByName);
        }
        // If name is empty, query for all products
        return queryProducts();
    }

    // Method for handling queries ready to be passed to database
    private ObservableList<Product> handleQuery(PreparedStatement query) {
        // Init empty list of products that will be returned from quering database
        ObservableList<Product> products = FXCollections.observableArrayList();
        try {
            // Get set of values from database
            ResultSet result = query.executeQuery();
            // Generate products based on values got from database
            while (result.next()) {
                String name = result.getString(1);
                int kcal = result.getInt(2);
                int protein = result.getInt(3);
                int carbo = result.getInt(4);
                int fat = result.getInt(5);
                String imgName = result.getString(6);
                // Generate new product and add it to list which will be returned
                products.add(Product.newProduct(name, kcal, protein, carbo, fat, imgName));
            }
        } catch (SQLException e) {
            Alerts.showError("Error quering database");
        }
        // Return list of products generated from values got from database
        return products;
    }
}
