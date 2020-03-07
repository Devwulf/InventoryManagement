package InventoryManagement.Controllers;

import InventoryManagement.Data.Inventory;
import InventoryManagement.Models.Part;
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
    public enum SearchFilter
    {
        Id("By Id"),
        Name("By Name");

        private String value;

        SearchFilter(String value)
        {
            this.value = value;
        }

        public String toString()
        {
            return this.value;
        }
    }

    public enum SortColumn
    {
        None,
        SortByIdAsc,
        SortByIdDesc,
        SortByNameAsc,
        SortByNameDesc,
        SortByInvAsc,
        SortByInvDesc,
        SortByPriceAsc,
        SortByPriceDesc
    }

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

    @FXML private TextField partSearchField;
    @FXML private ChoiceBox<SearchFilter> partSearchFilter;

    @FXML private Label partIdSort;
    @FXML private Label partNameSort;
    @FXML private Label partInvSort;
    @FXML private Label partPriceSort;

    @FXML private GridPane partsGrid;
    private HashMap<Integer, GridPane> partRows = new HashMap<>();

    private SortColumn partSort = SortColumn.SortByIdAsc;

    // The part id that was recently searched
    private int searchedPartId = 0;
    // The part name that was recently searched
    private String searchedPartName = "";

    // The part Id for the clicked row
    private int selectedPartId = 0;

    @FXML
    public void initialize()
    {
        populatePartsGrid();

        // Fill in searchFilter Choice Box and set default value
        partSearchFilter.getItems()
                        .setAll(SearchFilter.values());
        partSearchFilter.setValue(SearchFilter.Id);
    }

    @FXML
    public void handlePartSearch()
    {
        String input = partSearchField.getText();
        SearchFilter filter = partSearchFilter.getValue();
        if (filter == SearchFilter.Id)
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
        if (partSort != SortColumn.SortByIdAsc &&
                partSort != SortColumn.SortByIdDesc)
            partSort = SortColumn.SortByIdAsc;
        else if (partSort == SortColumn.SortByIdAsc)
            partSort = SortColumn.SortByIdDesc;
        else
            partSort = SortColumn.None;

        populatePartsGrid();
    }

    @FXML
    public void handleSortPartByName()
    {
        if (partSort != SortColumn.SortByNameAsc &&
                partSort != SortColumn.SortByNameDesc)
            partSort = SortColumn.SortByNameAsc;
        else if (partSort == SortColumn.SortByNameAsc)
            partSort = SortColumn.SortByNameDesc;
        else
            partSort = SortColumn.None;

        populatePartsGrid();
    }

    @FXML
    public void handleSortPartByInv()
    {
        if (partSort != SortColumn.SortByInvAsc &&
                partSort != SortColumn.SortByInvDesc)
            partSort = SortColumn.SortByInvAsc;
        else if (partSort == SortColumn.SortByInvAsc)
            partSort = SortColumn.SortByInvDesc;
        else
            partSort = SortColumn.None;

        populatePartsGrid();
    }

    @FXML
    public void handleSortPartByPrice()
    {
        if (partSort != SortColumn.SortByPriceAsc &&
                partSort != SortColumn.SortByPriceDesc)
            partSort = SortColumn.SortByPriceAsc;
        else if (partSort == SortColumn.SortByPriceAsc)
            partSort = SortColumn.SortByPriceDesc;
        else
            partSort = SortColumn.None;

        populatePartsGrid();
    }

    // These two methods are for hovering over the column headers
    @FXML
    public void handleHoverEnter()
    {
        ViewManager.getInstance()
                   .setCursor(Cursor.HAND);
    }

    @FXML
    public void handleHoverExit()
    {
        ViewManager.getInstance()
                   .setCursor(Cursor.DEFAULT);
    }

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
                    .setStyle(Constants.DESELECTED_ROW);

        selectedPartId = id;
        partRows.get(selectedPartId)
                .setStyle(Constants.SELECTED_ROW);
    }

    @FXML
    public void handleExit()
    {
        System.exit(0);
    }

    public void reloadView()
    {
        populatePartsGrid();
        // eventually populateProductsGrid();
    }

    // For sorting columns
    private void sortPartById(PartsWrapper partWrapper)
    {
        Comparator<Part> comparator;
        if (partSort == SortColumn.SortByIdAsc)
        {
            // update visual
            partIdSort.setText(Constants.UP_ARROW_CHAR); // up arrow unicode
            comparator = Comparator.comparingInt(Part::getId);
        }
        else if (partSort == SortColumn.SortByIdDesc)
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
        if (partSort == SortColumn.SortByNameAsc)
        {
            partNameSort.setText(Constants.UP_ARROW_CHAR);
            comparator = Comparator.comparing(Part::getName);
        }
        else if (partSort == SortColumn.SortByNameDesc)
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
        if (partSort == SortColumn.SortByInvAsc)
        {
            partInvSort.setText(Constants.UP_ARROW_CHAR);
            comparator = Comparator.comparingInt(Part::getStock);
        }
        else if (partSort == SortColumn.SortByInvDesc)
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
        if (partSort == SortColumn.SortByPriceAsc)
        {
            partPriceSort.setText(Constants.UP_ARROW_CHAR);
            comparator = Comparator.comparing(Part::getPrice);
        }
        else if (partSort == SortColumn.SortByPriceDesc)
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
                   .setPadding(new Insets(5))
                   .setStyle(Constants.LINE_BELOW);

        GridPaneBuilder gridPaneBuilder = new GridPaneBuilder();
        gridPaneBuilder.setStyle(Constants.DESELECTED_ROW);

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
}
