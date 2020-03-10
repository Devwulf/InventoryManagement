package InventoryManagement.Controllers;

import InventoryManagement.Data.Inventory;
import InventoryManagement.Models.Part;
import InventoryManagement.Models.Product;
import InventoryManagement.Services.ViewManager;
import InventoryManagement.Utils.*;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

public class MainController extends BaseController
{
    // Since Java can't pass by reference,
    // this is needed
    private class PartsWrapper
    {
        public ObservableList<Part> parts;

        public PartsWrapper(ObservableList<Part> parts)
        {
            this.parts = parts;
        }
    }

    private class ProductsWrapper
    {
        public ObservableList<Product> products;

        public ProductsWrapper(ObservableList<Product> products)
        {
            this.products = products;
        }
    }

    @FXML private TextField partSearchField;
    @FXML private ChoiceBox<Enums.SearchFilter> partSearchFilter;

    @FXML private Label partIdSort;
    @FXML private Label partNameSort;
    @FXML private Label partInvSort;
    @FXML private Label partPriceSort;

    @FXML private GridPane partsGrid;
    private HashMap<Integer, GridPane> partRows = new HashMap<>();

    private Enums.SortColumn partSort = Enums.SortColumn.SortByIdAsc;

    // The part id that was recently searched
    private int searchedPartId = 0;
    // The part name that was recently searched
    private String searchedPartName = "";

    // The part Id for the clicked row
    private int selectedPartId = 0;

    @FXML private TextField productSearchField;
    @FXML private ChoiceBox<Enums.SearchFilter> productSearchFilter;

    @FXML private Label productIdSort;
    @FXML private Label productNameSort;
    @FXML private Label productInvSort;
    @FXML private Label productPriceSort;

    @FXML private GridPane productsGrid;
    private HashMap<Integer, GridPane> productRows = new HashMap<>();

    private Enums.SortColumn productSort = Enums.SortColumn.SortByIdAsc;

    // The product id that was recently searched
    private int searchedProductId = 0;
    // The product name that was recently searched
    private String searchedProductName = "";

    // The product Id for the clicked row
    private int selectedProductId = 0;

    @FXML
    public void initialize()
    {
        populatePartsGrid();
        populateProductsGrid();

        // Fill in searchFilter Choice Box and set default value
        partSearchFilter.getItems()
                        .setAll(Enums.SearchFilter.values());
        partSearchFilter.setValue(Enums.SearchFilter.Id);

        productSearchFilter.getItems()
                           .setAll(Enums.SearchFilter.values());
        productSearchFilter.setValue(Enums.SearchFilter.Id);
    }

    // These two methods are for hovering over the column headers
    @FXML
    public void handleHoverEnter()
    {
        stage.getScene()
             .setCursor(Cursor.HAND);
    }

    @FXML
    public void handleHoverExit()
    {
        stage.getScene()
             .setCursor(Cursor.DEFAULT);
    }

    @FXML
    public void handleExit()
    {
        System.exit(0);
    }

    public void reloadView()
    {
        populatePartsGrid();
        populateProductsGrid();
    }

    // Parts

    @FXML
    public void handlePartSearch()
    {
        String input = partSearchField.getText();
        Enums.SearchFilter filter = partSearchFilter.getValue();
        if (filter == Enums.SearchFilter.Id)
        {
            try
            {
                int id = Integer.parseInt(input);
                filterByPartId(id);
            }
            catch (NumberFormatException e)
            {
                e.printStackTrace();

                ViewManager.getInstance()
                           .showWarningPopup("Only numbers are allowed when searching by id.");
            }
        }
        else
        {
            filterByPartName(input);
        }
    }

    private void filterByPartId(int id)
    {
        searchedPartName = "";
        searchedPartId = id;
        populatePartsGrid();
    }

    private void filterByPartName(String name)
    {
        searchedPartName = name;
        searchedPartId = 0;
        populatePartsGrid();
    }

    @FXML
    public void handleClearPartSearch()
    {
        searchedPartName = "";
        searchedPartId = 0;
        partSearchField.setText("");

        populatePartsGrid();
    }

    @FXML
    public void handleSortPartById()
    {
        if (partSort != Enums.SortColumn.SortByIdAsc &&
                partSort != Enums.SortColumn.SortByIdDesc)
            partSort = Enums.SortColumn.SortByIdAsc;
        else if (partSort == Enums.SortColumn.SortByIdAsc)
            partSort = Enums.SortColumn.SortByIdDesc;
        else
            partSort = Enums.SortColumn.None;

        populatePartsGrid();
    }

    @FXML
    public void handleSortPartByName()
    {
        if (partSort != Enums.SortColumn.SortByNameAsc &&
                partSort != Enums.SortColumn.SortByNameDesc)
            partSort = Enums.SortColumn.SortByNameAsc;
        else if (partSort == Enums.SortColumn.SortByNameAsc)
            partSort = Enums.SortColumn.SortByNameDesc;
        else
            partSort = Enums.SortColumn.None;

        populatePartsGrid();
    }

    @FXML
    public void handleSortPartByInv()
    {
        if (partSort != Enums.SortColumn.SortByInvAsc &&
                partSort != Enums.SortColumn.SortByInvDesc)
            partSort = Enums.SortColumn.SortByInvAsc;
        else if (partSort == Enums.SortColumn.SortByInvAsc)
            partSort = Enums.SortColumn.SortByInvDesc;
        else
            partSort = Enums.SortColumn.None;

        populatePartsGrid();
    }

    @FXML
    public void handleSortPartByPrice()
    {
        if (partSort != Enums.SortColumn.SortByPriceAsc &&
                partSort != Enums.SortColumn.SortByPriceDesc)
            partSort = Enums.SortColumn.SortByPriceAsc;
        else if (partSort == Enums.SortColumn.SortByPriceAsc)
            partSort = Enums.SortColumn.SortByPriceDesc;
        else
            partSort = Enums.SortColumn.None;

        populatePartsGrid();
    }

    // TODO: Disable when another AddPart window is already open
    @FXML
    public void handleAddPart()
    {
        ViewManager.getInstance()
                   .loadView(ViewManager.ViewNames.PartAdd, "Add Part");
    }

    @FXML
    public void handleModifyPart()
    {
        if (selectedPartId <= 0)
        {
            ViewManager.getInstance()
                       .showWarningPopup("No selected part to be modified!");
            return;
        }

        PartModifyController controller = ViewManager.getInstance()
                                                     .loadView(ViewManager.ViewNames.PartModify, "Modify Part");
        controller.initialize(selectedPartId);
    }

    @FXML
    public void handleDeletePart()
    {
        if (selectedPartId <= 0)
            return;

        ViewManager.getInstance()
                   .showConfirmPopup("Are you sure you want to delete this part?", () ->
                   {
                       Inventory.getInstance()
                                .deletePartById(selectedPartId);
                       Inventory.getInstance()
                                .saveChanges();

                       ViewManager.getInstance()
                                  .reloadMainView();
                   });
    }

    // This is not referenced from FXML directly
    public void handleSelectPart(int id)
    {
        // Means another row is currently selected, turn its
        // background color to normal
        if (selectedPartId > 0)
            partRows.get(selectedPartId)
                    .setStyle(Constants.DESELECTED_ROW + Constants.LINE_BELOW);

        selectedPartId = id;
        partRows.get(selectedPartId)
                .setStyle(Constants.SELECTED_ROW);
    }

    // For sorting columns
    private void sortPartById(PartsWrapper partWrapper)
    {
        Comparator<Part> comparator;
        if (partSort == Enums.SortColumn.SortByIdAsc)
        {
            // update visual
            partIdSort.setText(Constants.UP_ARROW_CHAR); // up arrow unicode
            comparator = Comparator.comparingInt(Part::getId);
        }
        else if (partSort == Enums.SortColumn.SortByIdDesc)
        {
            partIdSort.setText(Constants.DOWN_ARROW_CHAR); // down arrow unicode
            comparator = Comparator.comparingInt(Part::getId)
                                   .reversed();
        }
        else
        {
            // we're not supposed to sort by this column
            partIdSort.setText(Constants.FILTER_CHAR); // upside down A unicode
            return;
        }

        partWrapper.parts = partWrapper.parts.stream()
                                             .sorted(comparator)
                                             .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    private void sortPartByName(PartsWrapper partWrapper)
    {
        Comparator<Part> comparator;
        if (partSort == Enums.SortColumn.SortByNameAsc)
        {
            partNameSort.setText(Constants.UP_ARROW_CHAR);
            comparator = Comparator.comparing(Part::getName);
        }
        else if (partSort == Enums.SortColumn.SortByNameDesc)
        {
            partNameSort.setText(Constants.DOWN_ARROW_CHAR);
            comparator = Comparator.comparing(Part::getName)
                                   .reversed();
        }
        else
        {
            partNameSort.setText(Constants.FILTER_CHAR);
            return;
        }

        partWrapper.parts = partWrapper.parts.stream()
                                             .sorted(comparator)
                                             .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    private void sortPartByInv(PartsWrapper partWrapper)
    {
        Comparator<Part> comparator;
        if (partSort == Enums.SortColumn.SortByInvAsc)
        {
            partInvSort.setText(Constants.UP_ARROW_CHAR);
            comparator = Comparator.comparingInt(Part::getStock);
        }
        else if (partSort == Enums.SortColumn.SortByInvDesc)
        {
            partInvSort.setText(Constants.DOWN_ARROW_CHAR);
            comparator = Comparator.comparingInt(Part::getStock)
                                   .reversed();
        }
        else
        {
            partInvSort.setText(Constants.FILTER_CHAR);
            return;
        }

        partWrapper.parts = partWrapper.parts.stream()
                                             .sorted(comparator)
                                             .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    private void sortPartByPrice(PartsWrapper partWrapper)
    {
        Comparator<Part> comparator;
        if (partSort == Enums.SortColumn.SortByPriceAsc)
        {
            partPriceSort.setText(Constants.UP_ARROW_CHAR);
            comparator = Comparator.comparing(Part::getPrice);
        }
        else if (partSort == Enums.SortColumn.SortByPriceDesc)
        {
            partPriceSort.setText(Constants.DOWN_ARROW_CHAR);
            comparator = Comparator.comparing(Part::getPrice)
                                   .reversed();
        }
        else
        {
            partPriceSort.setText(Constants.FILTER_CHAR);
            return;
        }

        partWrapper.parts = partWrapper.parts.stream()
                                             .sorted(comparator)
                                             .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    private void populatePartsGrid()
    {
        ObservableList<Part> parts;
        if (!searchedPartName.isEmpty())
            parts = Inventory.getInstance()
                             .lookupPart(searchedPartName);
        else if (searchedPartId > 0)
        {
            Part part = Inventory.getInstance()
                                 .lookupPart(searchedPartId);
            if (part != null)
                parts = FXCollections.observableArrayList(part);
            else
                parts = FXCollections.observableArrayList();
        }
        else
            parts = Inventory.getInstance()
                             .getAllParts();

        populatePartsGrid(parts);
    }

    private void populatePartsGrid(ObservableList<Part> parts)
    {
        selectedPartId = 0;

        PartsWrapper wrapper = new PartsWrapper(parts);

        // Each of these check if it's supposed to sort
        sortPartById(wrapper);
        sortPartByName(wrapper);
        sortPartByInv(wrapper);
        sortPartByPrice(wrapper);

        // Clear current grid
        partsGrid.getChildren()
                 .clear();
        partsGrid.getRowConstraints()
                 .clear();

        // Setting default configs
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setMinHeight(28);
        rowConstraints.setPrefHeight(28);
        rowConstraints.setMaxHeight(28);
        rowConstraints.setVgrow(Priority.SOMETIMES);

        // The defaults are good enough for this
        TextBuilder textBuilder = new TextBuilder();

        HBoxBuilder hBoxBuilder = new HBoxBuilder();
        hBoxBuilder.setPrefHeight(28)
                   .setPadding(new Insets(5));

        GridPaneBuilder gridPaneBuilder = new GridPaneBuilder();
        gridPaneBuilder.setStyle(Constants.DESELECTED_ROW + Constants.LINE_BELOW);

        // Filling in the grid pane
        for (int i = 0; i < wrapper.parts.size(); i++)
        {
            Part part = wrapper.parts.get(i);

            Text id = textBuilder.setText(Integer.toString(part.getId()))
                                 .build();
            HBox idCell = hBoxBuilder.build(id);

            Text name = textBuilder.setText(part.getName())
                                   .build();
            HBox nameCell = hBoxBuilder.build(name);

            Text stock = textBuilder.setText(Integer.toString(part.getStock()))
                                    .build();
            HBox stockCell = hBoxBuilder.build(stock);

            Text price = textBuilder.setText("$" + String.format("%.2f", part.getPrice())) // 2 decimal places
                                    .build();
            HBox priceCell = hBoxBuilder.build(price);

            GridPane grid = gridPaneBuilder.build();
            grid.addRow(0, idCell, nameCell, stockCell, priceCell);
            grid.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> handleSelectPart(part.getId()));
            partRows.put(part.getId(), grid);

            partsGrid.addRow(i, grid);
            partsGrid.getRowConstraints()
                     .add(rowConstraints);
        }
    }

    // Products

    @FXML
    public void handleProductSearch()
    {
        String input = productSearchField.getText();
        Enums.SearchFilter filter = productSearchFilter.getValue();
        if (filter == Enums.SearchFilter.Id)
        {
            try
            {
                int id = Integer.parseInt(input);
                filterByProductId(id);
            }
            catch (NumberFormatException e)
            {
                e.printStackTrace();

                ViewManager.getInstance()
                           .showWarningPopup("Only numbers are allowed when searching by id.");
            }
        }
        else
        {
            filterByProductName(input);
        }
    }

    private void filterByProductId(int id)
    {
        searchedProductName = "";
        searchedProductId = id;
        populateProductsGrid();
    }

    private void filterByProductName(String name)
    {
        searchedProductName = name;
        searchedProductId = 0;
        populateProductsGrid();
    }

    @FXML
    public void handleClearProductSearch()
    {
        searchedProductName = "";
        searchedProductId = 0;
        productSearchField.setText("");

        populateProductsGrid();
    }

    @FXML
    public void handleSortProductById()
    {
        if (productSort != Enums.SortColumn.SortByIdAsc &&
                productSort != Enums.SortColumn.SortByIdDesc)
            productSort = Enums.SortColumn.SortByIdAsc;
        else if (productSort == Enums.SortColumn.SortByIdAsc)
            productSort = Enums.SortColumn.SortByIdDesc;
        else
            productSort = Enums.SortColumn.None;

        populateProductsGrid();
    }

    @FXML
    public void handleSortProductByName()
    {
        if (productSort != Enums.SortColumn.SortByNameAsc &&
                productSort != Enums.SortColumn.SortByNameDesc)
            productSort = Enums.SortColumn.SortByNameAsc;
        else if (productSort == Enums.SortColumn.SortByNameAsc)
            productSort = Enums.SortColumn.SortByNameDesc;
        else
            productSort = Enums.SortColumn.None;

        populateProductsGrid();
    }

    @FXML
    public void handleSortProductByInv()
    {
        if (productSort != Enums.SortColumn.SortByInvAsc &&
                productSort != Enums.SortColumn.SortByInvDesc)
            productSort = Enums.SortColumn.SortByInvAsc;
        else if (productSort == Enums.SortColumn.SortByInvAsc)
            productSort = Enums.SortColumn.SortByInvDesc;
        else
            productSort = Enums.SortColumn.None;

        populateProductsGrid();
    }

    @FXML
    public void handleSortProductByPrice()
    {
        if (productSort != Enums.SortColumn.SortByPriceAsc &&
                productSort != Enums.SortColumn.SortByPriceDesc)
            productSort = Enums.SortColumn.SortByPriceAsc;
        else if (productSort == Enums.SortColumn.SortByPriceAsc)
            productSort = Enums.SortColumn.SortByPriceDesc;
        else
            productSort = Enums.SortColumn.None;

        populateProductsGrid();
    }

    @FXML
    public void handleAddProduct()
    {
        ViewManager.getInstance()
                   .loadView(ViewManager.ViewNames.ProductAdd, "Add Product");
    }

    @FXML
    public void handleModifyProduct()
    {
        if (selectedProductId <= 0)
        {
            ViewManager.getInstance()
                       .showWarningPopup("No selected product to be modified!");
            return;
        }

        ProductModifyController controller = ViewManager.getInstance()
                                                        .loadView(ViewManager.ViewNames.ProductModify, "Modify Product");
        controller.initialize(selectedProductId);
    }

    @FXML
    public void handleDeleteProduct()
    {
        if (selectedProductId <= 0)
            return;

        ViewManager.getInstance()
                   .showConfirmPopup("Are you sure you want to delete this product?", () ->
                   {
                       Inventory.getInstance()
                                .deleteProductById(selectedProductId);
                       Inventory.getInstance()
                                .saveChanges();

                       ViewManager.getInstance()
                                  .reloadMainView();
                   });
    }

    // This is not referenced from FXML directly
    public void handleSelectProduct(int id)
    {
        // Means another row is currently selected, turn its
        // background color to normal
        if (selectedProductId > 0)
            productRows.get(selectedProductId)
                       .setStyle(Constants.DESELECTED_ROW + Constants.LINE_BELOW);

        selectedProductId = id;
        productRows.get(selectedProductId)
                   .setStyle(Constants.SELECTED_ROW);
    }

    // For sorting columns
    private void sortProductById(ProductsWrapper productWrapper)
    {
        Comparator<Product> comparator;
        if (productSort == Enums.SortColumn.SortByIdAsc)
        {
            // update visual
            productIdSort.setText(Constants.UP_ARROW_CHAR); // up arrow unicode
            comparator = Comparator.comparingInt(Product::getId);
        }
        else if (productSort == Enums.SortColumn.SortByIdDesc)
        {
            productIdSort.setText(Constants.DOWN_ARROW_CHAR); // down arrow unicode
            comparator = Comparator.comparingInt(Product::getId)
                                   .reversed();
        }
        else
        {
            // we're not supposed to sort by this column
            productIdSort.setText(Constants.FILTER_CHAR); // upside down A unicode
            return;
        }

        productWrapper.products = productWrapper.products.stream()
                                                         .sorted(comparator)
                                                         .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    private void sortProductByName(ProductsWrapper productWrapper)
    {
        Comparator<Product> comparator;
        if (productSort == Enums.SortColumn.SortByNameAsc)
        {
            productNameSort.setText(Constants.UP_ARROW_CHAR);
            comparator = Comparator.comparing(Product::getName);
        }
        else if (productSort == Enums.SortColumn.SortByNameDesc)
        {
            productNameSort.setText(Constants.DOWN_ARROW_CHAR);
            comparator = Comparator.comparing(Product::getName)
                                   .reversed();
        }
        else
        {
            productNameSort.setText(Constants.FILTER_CHAR);
            return;
        }

        productWrapper.products = productWrapper.products.stream()
                                                         .sorted(comparator)
                                                         .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    private void sortProductByInv(ProductsWrapper productWrapper)
    {
        Comparator<Product> comparator;
        if (productSort == Enums.SortColumn.SortByInvAsc)
        {
            productInvSort.setText(Constants.UP_ARROW_CHAR);
            comparator = Comparator.comparingInt(Product::getStock);
        }
        else if (productSort == Enums.SortColumn.SortByInvDesc)
        {
            productInvSort.setText(Constants.DOWN_ARROW_CHAR);
            comparator = Comparator.comparingInt(Product::getStock)
                                   .reversed();
        }
        else
        {
            productInvSort.setText(Constants.FILTER_CHAR);
            return;
        }

        productWrapper.products = productWrapper.products.stream()
                                                         .sorted(comparator)
                                                         .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    private void sortProductByPrice(ProductsWrapper productWrapper)
    {
        Comparator<Product> comparator;
        if (productSort == Enums.SortColumn.SortByPriceAsc)
        {
            productPriceSort.setText(Constants.UP_ARROW_CHAR);
            comparator = Comparator.comparing(Product::getPrice);
        }
        else if (productSort == Enums.SortColumn.SortByPriceDesc)
        {
            productPriceSort.setText(Constants.DOWN_ARROW_CHAR);
            comparator = Comparator.comparing(Product::getPrice)
                                   .reversed();
        }
        else
        {
            productPriceSort.setText(Constants.FILTER_CHAR);
            return;
        }

        productWrapper.products = productWrapper.products.stream()
                                                         .sorted(comparator)
                                                         .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    private void populateProductsGrid()
    {
        ObservableList<Product> products;
        if (!searchedProductName.isEmpty())
            products = Inventory.getInstance()
                                .lookupProduct(searchedProductName);
        else if (searchedProductId > 0)
        {
            Product product = Inventory.getInstance()
                                       .lookupProduct(searchedProductId);
            if (product != null)
                products = FXCollections.observableArrayList(product);
            else
                products = FXCollections.observableArrayList();
        }
        else
            products = Inventory.getInstance()
                                .getAllProducts();

        populateProductsGrid(products);
    }

    private void populateProductsGrid(ObservableList<Product> products)
    {
        selectedProductId = 0;

        ProductsWrapper wrapper = new ProductsWrapper(products);

        // Each of these check if it's supposed to sort
        sortProductById(wrapper);
        sortProductByName(wrapper);
        sortProductByInv(wrapper);
        sortProductByPrice(wrapper);

        // Clear current grid
        productsGrid.getChildren()
                    .clear();
        productsGrid.getRowConstraints()
                    .clear();

        // Setting default configs
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setMinHeight(28);
        rowConstraints.setPrefHeight(28);
        rowConstraints.setMaxHeight(28);
        rowConstraints.setVgrow(Priority.SOMETIMES);

        // The defaults are good enough for this
        TextBuilder textBuilder = new TextBuilder();

        HBoxBuilder hBoxBuilder = new HBoxBuilder();
        hBoxBuilder.setPrefHeight(28)
                   .setPadding(new Insets(5));

        GridPaneBuilder gridPaneBuilder = new GridPaneBuilder();
        gridPaneBuilder.setStyle(Constants.DESELECTED_ROW + Constants.LINE_BELOW);

        // Filling in the grid pane
        for (int i = 0; i < wrapper.products.size(); i++)
        {
            Product product = wrapper.products.get(i);

            Text id = textBuilder.setText(Integer.toString(product.getId()))
                                 .build();
            HBox idCell = hBoxBuilder.build(id);

            Text name = textBuilder.setText(product.getName())
                                   .build();
            HBox nameCell = hBoxBuilder.build(name);

            Text stock = textBuilder.setText(Integer.toString(product.getStock()))
                                    .build();
            HBox stockCell = hBoxBuilder.build(stock);

            Text price = textBuilder.setText("$" + String.format("%.2f", product.getPrice())) // 2 decimal places
                                    .build();
            HBox priceCell = hBoxBuilder.build(price);

            GridPane grid = gridPaneBuilder.build();
            grid.addRow(0, idCell, nameCell, stockCell, priceCell);
            grid.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> handleSelectProduct(product.getId()));
            productRows.put(product.getId(), grid);

            productsGrid.addRow(i, grid);
            productsGrid.getRowConstraints()
                        .add(rowConstraints);
        }
    }
}
