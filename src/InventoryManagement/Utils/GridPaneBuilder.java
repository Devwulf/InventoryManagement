package InventoryManagement.Utils;

import javafx.geometry.Insets;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class GridPaneBuilder
{
    private GridPane gridPane;

    public GridPaneBuilder() {
        gridPane = new GridPane();
        gridPane.setPrefWidth(100);
        gridPane.setPrefHeight(100);
        gridPane.setPadding(new Insets(0));
        gridPane.setHgap(0);
        gridPane.setVgap(0);
    }

    public GridPaneBuilder setPrefWidth(double width) {
        gridPane.setPrefWidth(width);
        return this;
    }

    public GridPaneBuilder setPrefHeight(double height) {
        gridPane.setPrefHeight(height);
        return this;
    }

    public GridPaneBuilder setStyle(String style) {
        gridPane.setStyle(style);
        return this;
    }

    public GridPaneBuilder setPadding(Insets padding) {
        gridPane.setPadding(padding);
        return this;
    }

    public GridPaneBuilder setHgap(double hgap) {
        gridPane.setHgap(hgap);
        return this;
    }

    public GridPaneBuilder setVgap(double vgap) {
        gridPane.setVgap(vgap);
        return this;
    }

    public GridPane build() {
        GridPane newGridPane = new GridPane();
        newGridPane.setPrefWidth(gridPane.getPrefWidth());
        newGridPane.setPrefHeight(gridPane.getPrefHeight());
        newGridPane.setStyle(gridPane.getStyle());
        newGridPane.setPadding(gridPane.getPadding());
        newGridPane.setHgap(gridPane.getHgap());
        newGridPane.setVgap(gridPane.getVgap());

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(25);

        newGridPane.getColumnConstraints().clear();
        newGridPane.getColumnConstraints().add(columnConstraints);
        newGridPane.getColumnConstraints().add(columnConstraints);
        newGridPane.getColumnConstraints().add(columnConstraints);
        newGridPane.getColumnConstraints().add(columnConstraints);

        return newGridPane;
    }
}
