package InventoryManagement.Utils;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class HBoxBuilder
{
    private HBox hBox;

    public HBoxBuilder()
    {
        hBox = new HBox();
        hBox.setPrefWidth(100);
        hBox.setPrefHeight(100);
        hBox.setPadding(new Insets(12));
    }

    public HBoxBuilder setPrefWidth(double width)
    {
        hBox.setPrefWidth(width);
        return this;
    }

    public HBoxBuilder setPrefHeight(double height)
    {
        hBox.setPrefHeight(height);
        return this;
    }

    public HBoxBuilder setStyle(String style)
    {
        hBox.setStyle(style);
        return this;
    }

    public HBoxBuilder setPadding(Insets padding)
    {
        hBox.setPadding(padding);
        return this;
    }

    public HBox build()
    {
        return build(null);
    }

    public HBox build(Node... children)
    {
        HBox newHBox = new HBox(children);
        newHBox.setPrefWidth(hBox.getPrefWidth());
        newHBox.setPrefHeight(hBox.getPrefHeight());
        newHBox.setStyle(hBox.getStyle());
        newHBox.setPadding(hBox.getPadding());

        return newHBox;
    }
}
