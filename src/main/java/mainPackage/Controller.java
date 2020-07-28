package mainPackage;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.util.Callback;
import mainPackage.dataOperating.ProductData;
import mainPackage.dataOperating.QueryThread;
import mainPackage.dialogs.Alerts;

import java.io.IOException;
import java.util.Optional;

public final class Controller {
    // Lists of products available in database and list of products that user selected
    @FXML
    private ListView<Product> allProductsListView, chosenListView;
    @FXML
    private MenuBar menuBar;
    // Center labels for present product properties
    @FXML
    private Label caloriesLabel, proteinLabel, carboLabel, fatLabel, gramatureLabel, caloriesField, proteinField, carboField, fatField;
    // Context menus for products in lists
    @FXML
    private ContextMenu addProductContextMenu, chosenListProductContextMenu;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private TextField searchField;

    // Values used for specifing gramature dialog goal
    private enum gramatureDialogUseGoal {
        ADD, MODIFY
    }

    // Values for specifing which properties of chosen product will be displayed in center
    enum fillCenterLabelsValuesType {
        DEFAULT, RELATIVE
    }

    // Values for specifing operation which will be procceded after using gramature dialog window
    enum calculatePropertiesToChosenListSumOperation {
        ADD, SUBTRACT
    }

    // Default macro
    private int userListKcal = 0;
    private int userListProtein = 0;
    private int userListCarbo = 0;
    private int userListFat = 0;

    @FXML
    public void initialize() {
        // Initialize context menu for adding product to user list
        addProductContextMenu = new ContextMenu();
        MenuItem addToListMenuItem = new MenuItem("Add");
        addToListMenuItem.setOnAction(actionEvent -> useGramatureDialog(getSelectedProduct(allProductsListView), gramatureDialogUseGoal.MODIFY));
        addProductContextMenu.getItems().add(addToListMenuItem);
        // Initialize context menu for remove from user list or modify amount of product
        chosenListProductContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete from list");
        deleteMenuItem.setOnAction(actionEvent -> deleteItemFromUserList(getSelectedProduct(chosenListView)));
        MenuItem modifyMenuItem = new MenuItem("Modify");
        modifyMenuItem.setOnAction((actionEvent -> useGramatureDialog(getSelectedProduct(chosenListView), gramatureDialogUseGoal.MODIFY)));
        chosenListProductContextMenu.getItems().addAll(modifyMenuItem, deleteMenuItem);
        // Add listener for selected product in list of products in database
        allProductsListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Product> observableValue, Product product, Product t1) -> {
            if (t1 != null) {
                fillCenterLabels(chosenListView, allProductsListView, fillCenterLabelsValuesType.DEFAULT);
            }
        });
        // Set specification of products in database list
        allProductsListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Product> call(ListView<Product> productListView) {
                ListCell<Product> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(Product product, boolean b) {
                        super.updateItem(product, b);
                        // If cell is not empty
                        if (!b) {
                            // Setting specification of cell in database list
                            // Name of product
                            setText(product.getName());
                            // Image of product
                            setGraphic(new ImageView(new Image(getClass().getResource("styles/img/" + product.getImgName() + ".png").toExternalForm())));
                            // Double click handler for adding product to user list
                            setOnMouseClicked(mouseEvent -> {
                                if (mouseEvent.getButton() == MouseButton.PRIMARY)
                                    if (mouseEvent.getClickCount() == 2) {
                                        useGramatureDialog(getSelectedProduct(allProductsListView), gramatureDialogUseGoal.ADD);
                                    }
                            });
                            // Tooltip if mause hovered
                            setTooltip(new Tooltip("Double click to add to your list"));
                        } else {
                            // If cell is empty
                            setText(null);
                            setGraphic(null);
                        }
                    }
                };
                // Adding context menu for product cell
                cell.emptyProperty().addListener(
                        (bos, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(addProductContextMenu);
                            }
                        }
                );
                return cell;
            }
        });
        // Put products got from database in list
        allProductsListView.setItems(ProductData.getInstance().queryProducts());
        // Only one item can be selected at time
        allProductsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // Select first item by default
        allProductsListView.getSelectionModel().selectFirst();
        // Only one item can be selected at time
        chosenListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // Add listener for selected product in user list
        chosenListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Product> observableValue, Product product, Product t1) -> {
            if (t1 != null) {
                // Fill center area with properties based on gramature entered by user while adding product
                fillCenterLabels(allProductsListView, chosenListView, fillCenterLabelsValuesType.RELATIVE);
            }
        });
        // Set specification of products cells in user list
        chosenListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Product> call(ListView<Product> productListView) {
                ListCell<Product> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(Product product, boolean b) {
                        super.updateItem(product, b);
                        // If product cell if not empty
                        if (!b) {
                            // Display product name and gramature set by user
                            setText(product.getName() + "( " + product.getGramature() + " g )");
                            // Display image of product
                            setGraphic(new ImageView(new Image(getClass().getResource("styles/img/" + product.getImgName() + ".png").toExternalForm())));
                        } else {
                            // If cell is empty
                            setText(null);
                            setGraphic(null);
                        }
                        // Tooltip when mause hovered
                        setTooltip(new Tooltip("Double click to remove from list"));
                        // Double mause click will remove product from user list
                        setOnMouseClicked(mouseEvent -> {
                            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                                if (mouseEvent.getClickCount() == 2) {
                                    deleteItemFromUserList(getSelectedProduct(chosenListView));
                                }
                            }
                        });
                    }
                };
                // Set context menu for products in user list
                cell.emptyProperty().addListener(
                        (bos, wasEmpty, isNowEmpty) -> {
                            if (!isNowEmpty) {
                                cell.setContextMenu(chosenListProductContextMenu);
                            } else {
                                cell.setContextMenu(null);
                            }
                        }
                );

                return cell;
            }
        });
    }

    // Handle search field
    @FXML
    public void handleSearch() {
        // Initialize task which will properly query database for products
        QueryThread queryThread = (searchField.getText() != null) ?
                QueryThread.newQueryThread(searchField.getText().toLowerCase()) : QueryThread.newQueryThread();
        // Bind content of products list with query thread result
        allProductsListView.itemsProperty().bind(queryThread.valueProperty());
        new Thread(queryThread).start();
    }
    @FXML
    public void handleExitButton() {
        Platform.exit();
    }
    // Dialog which display info about author
    @FXML
    public void handleAboutAppButton() {
        Alerts.showInfo("Food calculator app made by Bartosz Bartosik");
    }
    // Dialog with picture showing how to use application
    @FXML
    public void showHelpDialog() {
        Alert helpAlert = new Alert(Alert.AlertType.INFORMATION);
        helpAlert.getDialogPane().setMaxWidth(611);
        helpAlert.setTitle("Help");
        helpAlert.setHeaderText(null);
        helpAlert.setContentText(null);
        helpAlert.setGraphic(new ImageView(new Image(getClass().getResource("styles/img/Help.png").toExternalForm())));
        helpAlert.showAndWait();
    }

    private void deleteItemFromUserList(Product product) {
        // Remove product from user list
        chosenListView.getItems().remove(product);
        // Subtract product macro from user macro
        calculatePropertiesToChosenListSum(product, calculatePropertiesToChosenListSumOperation.SUBTRACT);
        // Set default macro of product
        product.setDefaultValues();
        // Select first product in database list
        allProductsListView.getSelectionModel().selectFirst();
    }

    // Method to fill properties labels in center with selected product macro
    private void fillCenterLabels(ListView<Product> listToClear, ListView<Product> listToFill, fillCenterLabelsValuesType valuesType) {
        // Clear labels
        gramatureLabel.setText(null);
        caloriesLabel.setText(null);
        proteinLabel.setText(null);
        carboLabel.setText(null);
        fatLabel.setText(null);
        // Clear selection of previous product
        listToClear.getSelectionModel().clearSelection();
        // Get product, which properties will be presented in center
        Product selectedProduct = getSelectedProduct(listToFill);
        // Presenting default product macro
        if (valuesType == fillCenterLabelsValuesType.DEFAULT) {
            gramatureLabel.setText("Gramature in " + selectedProduct.getDEFAULT_GRAMATURE() + "g:");
            caloriesLabel.setText(String.valueOf(selectedProduct.getDEFAULT_KCAL()));
            proteinLabel.setText(String.valueOf(selectedProduct.getDEFAULT_PROTEIN()));
            carboLabel.setText(String.valueOf(selectedProduct.getDEFAULT_CARBO()));
            fatLabel.setText(String.valueOf(selectedProduct.getDEFAULT_FAT()));
            // Presenting macro of product based on gramature passed by user while adding it to user list
        } else if (valuesType == fillCenterLabelsValuesType.RELATIVE) {
            gramatureLabel.setText("Gramature in " + selectedProduct.getGramature() + "g:");
            caloriesLabel.setText(String.valueOf(selectedProduct.getKcal()));
            proteinLabel.setText(String.valueOf(selectedProduct.getProtein()));
            carboLabel.setText(String.valueOf(selectedProduct.getCarbo()));
            fatLabel.setText(String.valueOf(selectedProduct.getFat()));
        }
    }

    // Get selected item from proper list
    private Product getSelectedProduct(ListView<Product> list) {
        return list.getSelectionModel().getSelectedItem();
    }

    // Method which handle gramature dialog
    private void useGramatureDialog(Product product, gramatureDialogUseGoal goal) {
        Dialog<ButtonType> addDialog = new Dialog<>();
        addDialog.setTitle("Adding or modyfing amount of product in user list");
        addDialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("dialogs/addingDialog.fxml"));
        try {
            addDialog.getDialogPane().setContent(loader.load());
            addDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            addDialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            Optional<ButtonType> result = addDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                AddingDialogController controller = loader.getController();
                int gramature = controller.processResults();
                // If controller method does't return error value
                if (gramature != -1) {
                    if (goal.equals(gramatureDialogUseGoal.ADD)) {
                        // If product is not already on user list, add it
                        if (!findProductInUserList(product)) {
                            addProductToUserList(product, gramature);
                            // If product is already on user list, modify gramature of it
                        } else {
                            modifyAmountOfProductInUserList(product, product.getGramature() + (gramature));
                        }
                        // Modify gramature of product in user list
                    } else if (goal.equals(gramatureDialogUseGoal.MODIFY)) {
                        modifyAmountOfProductInUserList(product, gramature);
                    }
                    // If controller method returns error value, open dialog again
                } else {
                    if (goal.equals(gramatureDialogUseGoal.ADD))
                        useGramatureDialog(product, gramatureDialogUseGoal.ADD);
                    else
                        useGramatureDialog(product, gramatureDialogUseGoal.MODIFY);
                }
            }
        } catch (IOException e) {
            Alerts.showError("Cannot load dialog window, check if every of required files exists");
        }
    }

    private void addProductToUserList(Product product, int gramature) {
        // Modify the macro of product based of entered gramature
        product.setGramature(gramature);
        chosenListView.getItems().add(product);
        // Add product macro to user macro
        calculatePropertiesToChosenListSum(product, calculatePropertiesToChosenListSumOperation.ADD);
    }

    private void modifyAmountOfProductInUserList(Product product, int gramature) {
        // Subtract macro of product from user macro
        calculatePropertiesToChosenListSum(product, calculatePropertiesToChosenListSumOperation.SUBTRACT);
        // Set default values of product
        product.setDefaultValues();
        // Calculate new macro of product based of sum of previous and new gramature
        product.setGramature(gramature);
        // Add modified product macro to user macro
        calculatePropertiesToChosenListSum(product, calculatePropertiesToChosenListSumOperation.ADD);
        // Refresh selection to present macro of product in center
        chosenListView.getSelectionModel().clearSelection();
        chosenListView.getSelectionModel().select(product);
    }

    // Method to check if product is already in user list
    private boolean findProductInUserList(Product product) {
        boolean isFound = false;
        if (!chosenListView.getItems().isEmpty() && chosenListView.getItems().contains(product)) {
            isFound = true;
        }
        return isFound;
    }

    // Add or subtract product macro from user macro
    private void calculatePropertiesToChosenListSum(Product product, calculatePropertiesToChosenListSumOperation operation) {
        if (operation.equals(calculatePropertiesToChosenListSumOperation.ADD)) {
            userListKcal += product.getKcal();
            userListProtein += product.getProtein();
            userListCarbo += product.getCarbo();
            userListFat += product.getFat();
        } else if (operation.equals(calculatePropertiesToChosenListSumOperation.SUBTRACT)) {
            userListKcal -= product.getKcal();
            userListProtein -= product.getProtein();
            userListCarbo -= product.getCarbo();
            userListFat -= product.getFat();
        }
        caloriesField.setText(String.valueOf(userListKcal));
        proteinField.setText(String.valueOf(userListProtein));
        carboField.setText(String.valueOf(userListCarbo));
        fatField.setText(String.valueOf(userListFat));
    }

    // Menubar access for main class
    public MenuBar getMenuBar() {
        return menuBar;
    }
}
