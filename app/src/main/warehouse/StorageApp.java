package warehouse;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import warehouse.repository.DatabaseManager;
import warehouse.model.Product;
import warehouse.model.Rack;
import warehouse.model.RackType;
import warehouse.model.StorageZone;
import warehouse.model.Product.Builder;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class StorageApp extends Application {
    private DatabaseManager dbManager = new DatabaseManager();
    private Stage primaryStage;
    private Scene mainScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Warehouse Storage Management");
        mainScene = createMainScene();
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private Scene createMainScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Зоны склада (кнопки)
        VBox zoneBox = new VBox(10);
        zoneBox.setAlignment(Pos.CENTER);
        try {
            List<StorageZone> zones = dbManager.getAllZones();
            for (StorageZone zone : zones) {
                Button zoneButton = new Button(zone.getName());
                zoneButton.setPrefSize(200, 100);
                zoneButton.setOnAction(e -> primaryStage.setScene(createZoneScene(zone)));
                zoneBox.getChildren().add(zoneButton);
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load zones.");
        }

        // Поиск товаров
        HBox searchBox = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name...");
        Button searchButton = new Button("Search");
        searchBox.getChildren().addAll(new Label("Search:"), searchField, searchButton);

        // Таблица товаров
        TableView<Product> productTable = new TableView<>();
        TableColumn<Product, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getId()));
        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        TableColumn<Product, Double> widthCol = new TableColumn<>("Width");
        widthCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getWidth()).asObject());
        TableColumn<Product, Double> heightCol = new TableColumn<>("Height");
        heightCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getHeight()).asObject());
        TableColumn<Product, Double> depthCol = new TableColumn<>("Depth");
        depthCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getDepth()).asObject());
        TableColumn<Product, String> rackCol = new TableColumn<>("Rack ID");
        rackCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRackId()));
        TableColumn<Product, String> sizeCol = new TableColumn<>("Size");
        sizeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSize().toString()));
        productTable.getColumns().addAll(idCol, nameCol, widthCol, heightCol, depthCol, rackCol, sizeCol);
        

        // Загрузка данных в таблицу
        try {
            productTable.setItems(FXCollections.observableArrayList(dbManager.getAllProducts()));
        } catch (SQLException e) {
            showAlert("Error", "Failed to load products.");
        }

        // Обработчик поиска
        searchButton.setOnAction(e -> {
            try {
                String searchText = searchField.getText();
                if (searchText.isEmpty()) {
                    productTable.setItems(FXCollections.observableArrayList(dbManager.getAllProducts()));
                } else {
                    productTable.setItems(FXCollections.observableArrayList(dbManager.searchProductsByName(searchText)));
                }
            } catch (SQLException ex) {
                showAlert("Error", "Search failed.");
            }
        });

        // Форма для добавления/редактирования товара
        GridPane form = new GridPane();
        form.setVgap(10);
        form.setHgap(10);
        form.setPadding(new Insets(10));

        TextField nameField = new TextField();
        TextField widthField = new TextField();
        TextField heightField = new TextField();
        TextField depthField = new TextField();
        ComboBox<String> rackCombo = new ComboBox<>();
        try {
            List<Rack> racks = dbManager.getFreeRacks();
            rackCombo.setItems(FXCollections.observableArrayList(racks.stream().map(Rack::getId).toList()));
        } catch (SQLException e) {
            showAlert("Error", "Failed to load racks.");
        }

        form.add(new Label("Name:"), 0, 0);
        form.add(nameField, 1, 0);
        form.add(new Label("Width:"), 0, 1);
        form.add(widthField, 1, 1);
        form.add(new Label("Height:"), 0, 2);
        form.add(heightField, 1, 2);
        form.add(new Label("Depth:"), 0, 3);
        form.add(depthField, 1, 3);
        // Удаляем поле выбора стеллажа для добавления, так как оно теперь автоматическое
        form.add(new Label("Rack:"), 0, 4);
        form.add(rackCombo, 1, 4); // Оставляем ComboBox для редактирования

        // Информация о складе
        VBox warehouseInfoPane = new VBox(10);
        warehouseInfoPane.setPadding(new Insets(10));
        warehouseInfoPane.setStyle("-fx-border-color: black; -fx-border-width: 1;");
        Label warehouseInfoLabel = new Label();
        warehouseInfoPane.getChildren().add(new Label("Warehouse Info:"));
        warehouseInfoPane.getChildren().add(warehouseInfoLabel);
        updateWarehouseInfo(warehouseInfoLabel);

        // Кнопки управления
        Button addButton = new Button("Add Product");
        Button updateButton = new Button("Update Product");
        Button deleteButton = new Button("Delete Product");
        Button clearButton = new Button("Clear");
        Button balanceButton = new Button("Balance");

        HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton, clearButton, balanceButton);

        // Обработчики кнопок
        addButton.setOnAction(e -> {
            try {
                Builder builderProduct = new Builder();
                builderProduct.setId(UUID.randomUUID().toString());
                builderProduct.setName(nameField.getText());

                builderProduct.setWidth(Double.parseDouble(widthField.getText()));
                builderProduct.setHeight(Double.parseDouble(heightField.getText()));
                builderProduct.setDepth(Double.parseDouble(depthField.getText()));

                
                Product product = new Product(builderProduct);
                // Поиск подходящего стеллажа
                List<Rack> racks = dbManager.getFreeRacks();
                System.out.println("FIND RACK");
                for (Rack rack : racks) {
                    if (rack.getType() == product.getSize()) {
                        System.out.printf("Obj size: %s     Rack id: %s,  size: %s\n", product.getSize(), rack.getId(), rack.getType());
                        product.setRackId(rack.getId());
                        break;
                    }
                }                

                if (product.getRackId() == null) {
                    showAlert("Error", "No available rack of type " + product.getSize() + ".");
                    return;
                }
                dbManager.addProduct(product);
                productTable.setItems(FXCollections.observableArrayList(dbManager.getAllProducts()));
                racks = dbManager.getFreeRacks();
                rackCombo.setItems(FXCollections.observableArrayList(racks.stream().map(Rack::getId).toList()));
                clearForm(nameField, widthField, heightField, depthField, rackCombo);
                updateWarehouseInfo(warehouseInfoLabel);
            } catch (SQLException | NumberFormatException ex) {
                showAlert("Error", "Invalid input or database error.");
            } catch (IllegalArgumentException exception) {
                showAlert("Error", exception.getMessage());
                return;
            }
        });

        updateButton.setOnAction(e -> {
            Product selected = productTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    Product updatedProduct = new Product(
                            selected.getId(),
                            nameField.getText(),
                            Double.parseDouble(widthField.getText()),
                            Double.parseDouble(heightField.getText()),
                            Double.parseDouble(depthField.getText()),
                            rackCombo.getValue()
                    );
                    dbManager.updateProduct(updatedProduct);
                    productTable.setItems(FXCollections.observableArrayList(dbManager.getAllProducts()));
                    List<Rack> racks = dbManager.getFreeRacks();
                    rackCombo.setItems(FXCollections.observableArrayList(racks.stream().map(Rack::getId).toList()));
                    clearForm(nameField, widthField, heightField, depthField, rackCombo);
                    updateWarehouseInfo(warehouseInfoLabel);
                } catch (SQLException | NumberFormatException ex) {
                    showAlert("Error", "Invalid input or database error.");
                }
            }
        });

        deleteButton.setOnAction(e -> {
            Product selected = productTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    dbManager.deleteProduct(selected.getId());
                    productTable.setItems(FXCollections.observableArrayList(dbManager.getAllProducts()));
                    List<Rack> racks = dbManager.getFreeRacks();
                    rackCombo.setItems(FXCollections.observableArrayList(racks.stream().map(Rack::getId).toList()));
                    updateWarehouseInfo(warehouseInfoLabel);
                } catch (SQLException ex) {
                    showAlert("Error", "Database error.");
                }
            }
        });

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                widthField.setText(String.valueOf(newSelection.getWidth()));
                heightField.setText(String.valueOf(newSelection.getHeight()));
                depthField.setText(String.valueOf(newSelection.getDepth()));
                rackCombo.setValue(newSelection.getRackId());
                addButton.setVisible(false);
            }
        });

        clearButton.setOnAction(e -> {
            nameField.setText("");
            widthField.setText("");
            heightField.setText("");
            depthField.setText("");
            rackCombo.setValue("");
            addButton.setVisible(true);
        });

        balanceButton.setOnAction(e -> {

            nameField.setText("");
            widthField.setText("");
            heightField.setText("");
            depthField.setText("");
            rackCombo.setValue("");
            addButton.setVisible(true);

            try {
                dbManager.balanceProduct();
                productTable.setItems(FXCollections.observableArrayList(dbManager.getAllProducts()));
                updateWarehouseInfo(warehouseInfoLabel);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });

        // Правая панель (поиск, таблица, форма)
        HBox formAndInfoPane = new HBox(10, form, warehouseInfoPane);
        VBox rightPane = new VBox(10, searchBox, productTable, formAndInfoPane, buttonBox);
        // VBox rightPane = new VBox(10, searchBox, productTable, form, buttonBox);
        rightPane.setPrefWidth(500);

        // Сборка главного экрана
        root.setCenter(zoneBox);
        root.setRight(rightPane);

        return new Scene(root, 800, 600);
    }

    private Scene createZoneScene(StorageZone zone) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Кнопка возврата
        Button backButton = new Button("Back to Main");
        backButton.setOnAction(e -> primaryStage.setScene(mainScene));

        // Визуализация стеллажей
        Pane rackPane = new Pane();
        try {
            // Получение актуальных данных из базы
            List<Rack> racks = dbManager.getAllRacks().stream()
                    .filter(r -> r.getZoneId().equals(zone.getId()))
                    .toList();
            List<Product> products = dbManager.getAllProducts();

            // Параметры масштабирования
            final double RACK_WIDTH_PX = 150; // Фиксированная ширина стеллажа в пикселях
            final double RACK_HEIGHT_PX = 400; // Фиксированная высота стеллажа в пикселях
            final double MAX_RACK_WIDTH = RackType.LARGE.getMaxWidth(); // 2.0 метра
            final double MAX_RACK_HEIGHT = RackType.LARGE.getMaxHeight(); // 3.0 метра
            final double SCALE_X = RACK_WIDTH_PX / MAX_RACK_WIDTH; // Масштаб по ширине: 75 пикселей/метр
            final double SCALE_Y = RACK_HEIGHT_PX / MAX_RACK_HEIGHT; // Масштаб по высоте: 133.33 пикселя/метр

            int xOffset = 20;
            // Сначала добавляем все стеллажи
            for (Rack rack : racks) {
                // Отрисовка стеллажа
                Rectangle rackRect = new Rectangle(xOffset, 50, RACK_WIDTH_PX, RACK_HEIGHT_PX);
                rackRect.setFill(Color.LIGHTGRAY);
                rackRect.setStroke(Color.BLACK);

                Label rackLabel = new Label(rack.getId() + " (" + rack.getType() + ")");
                rackLabel.setLayoutX(xOffset);
                rackLabel.setLayoutY(30);
                rackPane.getChildren().addAll(rackRect, rackLabel);
                xOffset += RACK_WIDTH_PX + 20;
            }

            // Затем добавляем товары (чтобы они были поверх стеллажей)
            xOffset = 20;
            for (Rack rack : racks) {
                // Отрисовка товаров на стеллаже
                List<Product> rackProducts = products.stream()
                        .filter(p -> p.getRackId() != null && p.getRackId().equals(rack.getId()))
                        .toList();
                System.out.println("Rack: " + rack.getId() + ", Products found: " + rackProducts.size());

                double yOffset = 60;
                for (Product product : rackProducts) {
                    // Масштабирование размеров товара
                    double productWidthPx = product.getWidth() * SCALE_X;
                    double productHeightPx = product.getHeight() * SCALE_Y;

                    // Ограничение размеров для видимости
                    productWidthPx = Math.min(productWidthPx, RACK_WIDTH_PX - 20);
                    productHeightPx = Math.min(productHeightPx, 50);

                    System.out.println("Product: " + product.getName() + ", X: " + (xOffset + 10) + ", Y: " + yOffset + ", Width: " + productWidthPx + "px, Height: " + productHeightPx + "px");

                    Rectangle productRect = new Rectangle(xOffset + 10, yOffset, productWidthPx, productHeightPx);
                    productRect.setFill(Color.BLUE);
                    productRect.setStroke(Color.BLACK);
                    productRect.setOnMouseClicked(e -> showProductInfo(product));

                    // Добавляем текстовую метку для товара
                    Text productLabel = new Text(xOffset + 10, yOffset - 5, product.getName());
                    productLabel.setFill(Color.BLACK);

                    rackPane.getChildren().addAll(productRect, productLabel);
                    yOffset += productHeightPx + 20; // Отступ между товарами
                }
                xOffset += RACK_WIDTH_PX + 20;
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load racks or products: " + e.getMessage());
        }

        root.setTop(backButton);
        root.setCenter(rackPane);

        return new Scene(root, 800, 600);
    }

    private void updateWarehouseInfo(Label warehouseInfoLabel) {
        try {
            List<Product> products = dbManager.getAllProducts();
            List<Rack> racks = dbManager.getAllRacks();

            // Общее количество товаров
            int totalProducts = products.size();

            // Занятое место (суммарный объем товаров)
            double occupiedVolume = products.stream()
                    .mapToDouble(p -> p.getWidth() * p.getHeight() * p.getDepth())
                    .sum();

            // Максимальный объем стеллажей
            double totalRackVolume = racks.stream()
                    .mapToDouble(r -> r.getType().getMaxWidth() * r.getType().getMaxHeight() * r.getType().getMaxDepth())
                    .sum();

            // Свободное место
            double freeVolume = totalRackVolume - occupiedVolume;

            warehouseInfoLabel.setText(
                    "Total Products: " + totalProducts + "\n" +
                    String.format("Occupied Volume: %.2f cubic meters\n", occupiedVolume) +
                    String.format("Free Volume: %.2f cubic meters\n", freeVolume) +
                    String.format("Total Capacity: %.2f cubic meters", totalRackVolume)
            );
        } catch (SQLException e) {
            warehouseInfoLabel.setText("Error loading warehouse info.");
        }
    }

    private void showProductInfo(Product product) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Product Info");
        alert.setHeaderText(product.getName());
        alert.setContentText(
                "ID: " + product.getId() + "\n" +
                "Width: " + product.getWidth() + "\n" +
                "Height: " + product.getHeight() + "\n" +
                "Depth: " + product.getDepth() + "\n" +
                "Rack ID: " + product.getRackId()
        );
        alert.showAndWait();
    }

    private void clearForm(TextField name, TextField width, TextField height, TextField depth, ComboBox<String> rack) {
        name.clear();
        width.clear();
        height.clear();
        depth.clear();
        rack.setValue(null);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}