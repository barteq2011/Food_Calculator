package mainPackage.dataOperating;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import mainPackage.Product;

public class QueryThread extends Task<ObservableList<Product>> {
    // String wich will be used while searching for spefific product
    private final String searchProductName;

    private QueryThread(String searchProductName) {
        this.searchProductName = searchProductName;
    }

    // Thread which will be quering for all products
    public static QueryThread newQueryThread() {
        return new QueryThread(null);
    }

    // Thread which will be quering for products matching with entered name
    public static QueryThread newQueryThread(String searchProductName) {
        return new QueryThread(searchProductName);
    }

    @Override
    protected ObservableList<Product> call() {
        if (searchProductName != null)
            return ProductData.getInstance().queryProducts(searchProductName);
        return ProductData.getInstance().queryProducts();
    }
}
