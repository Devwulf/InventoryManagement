package InventoryManagement.Controllers;

import InventoryManagement.Data.Inventory;
import InventoryManagement.Models.InHouse;
import InventoryManagement.Models.Outsourced;
import InventoryManagement.Models.Part;
import InventoryManagement.Models.Product;
import InventoryManagement.Services.ViewManager;
import InventoryManagement.Utils.*;
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
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ProductModifyController extends BaseController
{
    private class PartsWrapper
    {
        public ObservableList<Part> parts;

        public PartsWrapper(ObservableList<Part> parts)
        {
            this.parts = parts;
        }
    }

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField invField;
    @FXML private TextField priceField;
    @FXML private TextField maxField;
    @FXML private TextField minField;

    @FXML private TextField partSearchField;
    @FXML private ChoiceBox<Enums.SearchFilter> partSearchFilter;

    @FXML private Label partIdSort;
    @FXML private Label partNameSort;
    @FXML private Label partInvSort;
    @FXML private Label partPriceSort;

    @FXML private GridPane partsGrid;
    private HashMap<Integer, GridPane> partRows = new HashMap<>();

    private Enums.SortColumn partSort = Enums.SortColumn.SortByIdAsc;

    private int searchedPartId = 0;
    private String searchedPartName = "";

    private int selectedPartId = 0;

    @FXML private Label productPartIdSort;
    @FXML private Label productPartNameSort;
    @FXML private Label productPartInvSort;
    @FXML private Label productPartPriceSort;

    @FXML private GridPane productPartsGrid;
    private HashMap<Integer, GridPane> productPartRows = new HashMap<>();

    private Enums.SortColumn productPartSort = Enums.SortColumn.SortByIdAsc;

    private int selectedProductPartId = 0;

    private ObservableList<Part> localPartsList;
    private ObservableList<Part> localProductPartsList;

    private int min = 0, max = 0;
    private double price = 0;

    public void initialize(int productId)
    {
        Product product = Inventory.getInstance()
                                   .lookupProduct(productId);

        // initialize only once
        // creates a copy of the real parts list
        // Note: this won't create copies of the objects in the real list
        localPartsList = FXCollections.observableArrayList(Inventory.getInstance()
                                                                    .getAllParts());
        localProductPartsList = FXCollections.observableArrayList(product.getAllAssociatedParts());

        // Setting the textfield styles for validation
        nameField.textProperty()
                 .addListener((observable, oldValue, newValue) ->
                 {
                     if (Constants.NOT_EMPTY_PATTERN.matcher(newValue)
                                                    .matches())
                     {
                         nameField.setStyle(Constants.VALID_STYLE);
                     }
                     else
                     {
                         nameField.setStyle(Constants.INVALID_STYLE);
                     }
                 });

        invField.textProperty()
                .addListener((observable, oldValue, newValue) ->
                {
                    if (isInvValid(newValue))
                    {
                        invField.setStyle(Constants.VALID_STYLE);
                    }
                    else
                    {
                        invField.setStyle(Constants.INVALID_STYLE);
                    }
                });

        priceField.textProperty()
                  .addListener((observable, oldValue, newValue) ->
                  {
                      if (isPriceValid(newValue))
                      {
                          priceField.setStyle(Constants.VALID_STYLE);
                      }
                      else
                      {
                          priceField.setStyle(Constants.INVALID_STYLE);
                      }
                  });

        maxField.textProperty()
                .addListener((observable, oldValue, newValue) ->
                {
                    if (isMaxValid(newValue))
                    {
                        maxField.setStyle(Constants.VALID_STYLE);
                    }
                    else
                    {
                        maxField.setStyle(Constants.INVALID_STYLE);
                    }
                });

        minField.textProperty()
                .addListener((observable, oldValue, newValue) ->
                {
                    if (isMinValid(newValue))
                    {
                        minField.setStyle(Constants.VALID_STYLE);
                    }
                    else
                    {
                        minField.setStyle(Constants.INVALID_STYLE);
                    }
                });

        partSearchFilter.getItems()
                        .setAll(Enums.SearchFilter.values());
        partSearchFilter.setValue(Enums.SearchFilter.Id);

        idField.setText(Integer.toString(product.getId()));
        nameField.setText(product.getName());
        invField.setText(Integer.toString(product.getStock()));
        priceField.setText(Double.toString(product.getPrice()));
        maxField.setText(Integer.toString(product.getMax()));
        minField.setText(Integer.toString(product.getMin()));

        populatePartsGrid();
        populateProductPartsGrid();
    }

    private boolean isPriceValid(String newValue)
    {
        boolean matches = Constants.MONEY_PATTERN.matcher(newValue)
                                                 .matches();
        double price = matches ? Double.parseDouble(newValue) : 0;

        if (!matches)
            return false;

        if (localProductPartsList.size() <= 0)
            return false;

        double partsPriceSum = 0;
        for (Part part : localProductPartsList)
            partsPriceSum += part.getPrice();

        if (price < partsPriceSum)
            return false;

        return true;
    }

    private boolean isMinValid(String newValue)
    {
        boolean matches = Constants.NUMBERS_ONLY_PATTERN.matcher(newValue)
                                                        .matches();
        min = matches ? Integer.parseInt(newValue) : 0;

        return matches && min <= max;
    }

    private boolean isMaxValid(String newValue)
    {
        boolean matches = Constants.NUMBERS_ONLY_PATTERN.matcher(newValue)
                                                        .matches();
        max = matches ? Integer.parseInt(newValue) : 0;

        return matches && max >= min;
    }

    private boolean isInvValid(String newValue)
    {
        boolean matches = Constants.NUMBERS_ONLY_PATTERN.matcher(newValue)
                                                        .matches();
        int inv = matches ? Integer.parseInt(newValue) : 0;

        return matches && inv >= min && inv <= max;
    }

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
    public void handleSave()
    {
        if (localProductPartsList.size() <= 0)
        {
            ViewManager.getInstance()
                       .showWarningPopup("The product must have at least one part.");
            return;
        }

        if (!Constants.NOT_EMPTY_PATTERN.matcher(nameField.getText())
                                        .matches())
        {
            ViewManager.getInstance()
                       .showWarningPopup("The product must have a name.");
            return;
        }

        if (!isMaxValid(maxField.getText()))
        {
            ViewManager.getInstance()
                       .showWarningPopup("The product's max must be a number and must be >= min.");
            return;
        }

        if (!isMinValid(minField.getText()))
        {
            ViewManager.getInstance()
                       .showWarningPopup("The product's min must be a number and must be <= max.");
            return;
        }

        if (!isInvValid(invField.getText()))
        {
            ViewManager.getInstance()
                       .showWarningPopup("The product's inventory level must be a number, must be >= min, and must be <= max.");
            return;
        }

        if (!isPriceValid(priceField.getText()))
        {
            ViewManager.getInstance()
                       .showWarningPopup("The product's price must be a number or in money format, and the total price must be >= the sum of the price of its parts.");
            return;
        }

        try
        {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            int stock = Integer.parseInt(invField.getText());
            double price = Double.parseDouble(priceField.getText());
            int max = Integer.parseInt(maxField.getText());
            int min = Integer.parseInt(minField.getText());

            Product product = new Product(id, name, price, stock, min, max);

            product.clearAllAssociatedParts();
            for (Part part : localProductPartsList)
                product.addAssociatedPart(part);

            Inventory.getInstance()
                     .updateProductById(id, product);
            Inventory.getInstance()
                     .saveChanges();

            ViewManager.getInstance()
                       .reloadMainView();
            stage.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCancel()
    {
        ViewManager.getInstance()
                   .showConfirmPopup("There are unsaved changes. Cancel?", () ->
                   {
                       Inventory.getInstance()
                                .cancelChanges();
                       stage.close();
                   });
    }

    public void reloadView()
    {
        populatePartsGrid();
        populateProductPartsGrid();
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

    @FXML
    public void handleAddPart()
    {
        if (selectedPartId <= 0)
            return;

        Part part = localPartsList.stream()
                                  .filter(part1 -> part1.getId() == selectedPartId)
                                  .findFirst()
                                  .orElse(null);
        if (part == null)
            return;

        Inventory.getInstance()
                 .deletePartById(selectedPartId);

        localPartsList.remove(part);
        localProductPartsList.add(part);
        reloadView();
    }

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
        {
            parts = localPartsList.stream()
                                  .filter(part -> part.getName()
                                                      .toLowerCase()
                                                      .contains(searchedPartName.toLowerCase()))
                                  .collect(Collectors.toCollection(FXCollections::observableArrayList));
        }
        else if (searchedPartId > 0)
        {
            Part part = localPartsList.stream()
                                      .filter(part1 -> part1.getId() == searchedPartId)
                                      .findFirst()
                                      .orElse(null);
            if (part != null)
                parts = FXCollections.observableArrayList(part);
            else
                parts = FXCollections.observableArrayList();
        }
        else
            parts = localPartsList;

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

    // Product Parts

    @FXML
    public void handleSortProductPartById()
    {
        if (productPartSort != Enums.SortColumn.SortByIdAsc &&
                productPartSort != Enums.SortColumn.SortByIdDesc)
            productPartSort = Enums.SortColumn.SortByIdAsc;
        else if (productPartSort == Enums.SortColumn.SortByIdAsc)
            productPartSort = Enums.SortColumn.SortByIdDesc;
        else
            productPartSort = Enums.SortColumn.None;

        populateProductPartsGrid();
    }

    @FXML
    public void handleSortProductPartByName()
    {
        if (productPartSort != Enums.SortColumn.SortByNameAsc &&
                productPartSort != Enums.SortColumn.SortByNameDesc)
            productPartSort = Enums.SortColumn.SortByNameAsc;
        else if (productPartSort == Enums.SortColumn.SortByNameAsc)
            productPartSort = Enums.SortColumn.SortByNameDesc;
        else
            productPartSort = Enums.SortColumn.None;

        populateProductPartsGrid();
    }

    @FXML
    public void handleSortProductPartByInv()
    {
        if (productPartSort != Enums.SortColumn.SortByInvAsc &&
                productPartSort != Enums.SortColumn.SortByInvDesc)
            productPartSort = Enums.SortColumn.SortByInvAsc;
        else if (productPartSort == Enums.SortColumn.SortByInvAsc)
            productPartSort = Enums.SortColumn.SortByInvDesc;
        else
            productPartSort = Enums.SortColumn.None;

        populateProductPartsGrid();
    }

    @FXML
    public void handleSortProductPartByPrice()
    {
        if (productPartSort != Enums.SortColumn.SortByPriceAsc &&
                productPartSort != Enums.SortColumn.SortByPriceDesc)
            productPartSort = Enums.SortColumn.SortByPriceAsc;
        else if (productPartSort == Enums.SortColumn.SortByPriceAsc)
            productPartSort = Enums.SortColumn.SortByPriceDesc;
        else
            productPartSort = Enums.SortColumn.None;

        populateProductPartsGrid();
    }

    @FXML
    public void handleDeleteProductPart()
    {
        if (selectedProductPartId <= 0)
            return;

        Part part = localProductPartsList.stream()
                                         .filter(part1 -> part1.getId() == selectedProductPartId)
                                         .findFirst()
                                         .orElse(null);
        if (part == null)
            return;

        Inventory.getInstance()
                 .addPart(part, true);

        localProductPartsList.remove(part);
        localPartsList.add(part);
        reloadView();
    }

    public void handleSelectProductPart(int id)
    {
        // Means another row is currently selected, turn its
        // background color to normal
        if (selectedProductPartId > 0)
            productPartRows.get(selectedProductPartId)
                           .setStyle(Constants.DESELECTED_ROW + Constants.LINE_BELOW);

        selectedProductPartId = id;
        productPartRows.get(selectedProductPartId)
                       .setStyle(Constants.SELECTED_ROW);
    }

    // For sorting columns
    private void sortProductPartById(PartsWrapper partWrapper)
    {
        Comparator<Part> comparator;
        if (productPartSort == Enums.SortColumn.SortByIdAsc)
        {
            // update visual
            productPartIdSort.setText(Constants.UP_ARROW_CHAR); // up arrow unicode
            comparator = Comparator.comparingInt(Part::getId);
        }
        else if (productPartSort == Enums.SortColumn.SortByIdDesc)
        {
            productPartIdSort.setText(Constants.DOWN_ARROW_CHAR); // down arrow unicode
            comparator = Comparator.comparingInt(Part::getId)
                                   .reversed();
        }
        else
        {
            // we're not supposed to sort by this column
            productPartIdSort.setText(Constants.FILTER_CHAR); // upside down A unicode
            return;
        }

        partWrapper.parts = partWrapper.parts.stream()
                                             .sorted(comparator)
                                             .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    private void sortProductPartByName(PartsWrapper partWrapper)
    {
        Comparator<Part> comparator;
        if (productPartSort == Enums.SortColumn.SortByNameAsc)
        {
            productPartNameSort.setText(Constants.UP_ARROW_CHAR);
            comparator = Comparator.comparing(Part::getName);
        }
        else if (productPartSort == Enums.SortColumn.SortByNameDesc)
        {
            productPartNameSort.setText(Constants.DOWN_ARROW_CHAR);
            comparator = Comparator.comparing(Part::getName)
                                   .reversed();
        }
        else
        {
            productPartNameSort.setText(Constants.FILTER_CHAR);
            return;
        }

        partWrapper.parts = partWrapper.parts.stream()
                                             .sorted(comparator)
                                             .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    private void sortProductPartByInv(PartsWrapper partWrapper)
    {
        Comparator<Part> comparator;
        if (productPartSort == Enums.SortColumn.SortByInvAsc)
        {
            productPartInvSort.setText(Constants.UP_ARROW_CHAR);
            comparator = Comparator.comparingInt(Part::getStock);
        }
        else if (productPartSort == Enums.SortColumn.SortByInvDesc)
        {
            productPartInvSort.setText(Constants.DOWN_ARROW_CHAR);
            comparator = Comparator.comparingInt(Part::getStock)
                                   .reversed();
        }
        else
        {
            productPartInvSort.setText(Constants.FILTER_CHAR);
            return;
        }

        partWrapper.parts = partWrapper.parts.stream()
                                             .sorted(comparator)
                                             .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    private void sortProductPartByPrice(PartsWrapper partWrapper)
    {
        Comparator<Part> comparator;
        if (productPartSort == Enums.SortColumn.SortByPriceAsc)
        {
            productPartPriceSort.setText(Constants.UP_ARROW_CHAR);
            comparator = Comparator.comparing(Part::getPrice);
        }
        else if (productPartSort == Enums.SortColumn.SortByPriceDesc)
        {
            productPartPriceSort.setText(Constants.DOWN_ARROW_CHAR);
            comparator = Comparator.comparing(Part::getPrice)
                                   .reversed();
        }
        else
        {
            productPartPriceSort.setText(Constants.FILTER_CHAR);
            return;
        }

        partWrapper.parts = partWrapper.parts.stream()
                                             .sorted(comparator)
                                             .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    private void populateProductPartsGrid()
    {
        ObservableList<Part> parts;
        if (!searchedPartName.isEmpty())
        {
            parts = localProductPartsList.stream()
                                         .filter(part -> part.getName()
                                                             .toLowerCase()
                                                             .contains(searchedPartName.toLowerCase()))
                                         .collect(Collectors.toCollection(FXCollections::observableArrayList));
        }
        else if (searchedPartId > 0)
        {
            Part part = localProductPartsList.stream()
                                             .filter(part1 -> part1.getId() == searchedPartId)
                                             .findFirst()
                                             .orElse(null);
            if (part != null)
                parts = FXCollections.observableArrayList(part);
            else
                parts = FXCollections.observableArrayList();
        }
        else
            parts = localProductPartsList;

        populateProductPartsGrid(parts);
    }

    private void populateProductPartsGrid(ObservableList<Part> parts)
    {
        selectedProductPartId = 0;

        PartsWrapper wrapper = new PartsWrapper(parts);

        // Each of these check if it's supposed to sort
        sortProductPartById(wrapper);
        sortProductPartByName(wrapper);
        sortProductPartByInv(wrapper);
        sortProductPartByPrice(wrapper);

        // Clear current grid
        productPartsGrid.getChildren()
                        .clear();
        productPartsGrid.getRowConstraints()
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
            grid.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> handleSelectProductPart(part.getId()));
            productPartRows.put(part.getId(), grid);

            productPartsGrid.addRow(i, grid);
            productPartsGrid.getRowConstraints()
                            .add(rowConstraints);
        }
    }
}
