package InventoryManagement.Utils;

import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TextBuilder
{
    private Text text;

    public TextBuilder() {
        text = new Text();
        text.setText("");
        text.setFill(Paint.valueOf("black"));
        text.setFont(new Font(12.0));
    }

    public TextBuilder setText(String text)
    {
        this.text.setText(text);
        return this;
    }

    public TextBuilder setTextFill(Paint paint) {
        text.setFill(paint);
        return this;
    }

    public TextBuilder setFont(Font font) {
        text.setFont(font);
        return this;
    }

    public Text build() {
        Text newLabel = new Text();
        newLabel.setText(text.getText());
        newLabel.setFill(text.getFill());
        newLabel.setFont(text.getFont());

        return newLabel;
    }
}
