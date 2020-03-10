package InventoryManagement.Utils;

import java.util.regex.Pattern;

public final class Constants
{
    // Patterns for validation
    public static final Pattern MONEY_PATTERN = Pattern.compile("^[0-9]+((\\.[0-9])|(\\.[0-9][0-9]))?$");
    public static final Pattern NOT_EMPTY_PATTERN = Pattern.compile("^(.|\\s)*\\S(.|\\s)*$");
    public static final Pattern NUMBERS_ONLY_PATTERN = Pattern.compile("^[0-9]+$");

    // TextField style for visual validation
    public static final String VALID_STYLE = "-fx-border-width: 3px; -fx-border-radius: 3px; -fx-border-color: #28a745";
    public static final String INVALID_STYLE = "-fx-border-width: 3px; -fx-border-radius: 3px; -fx-border-color: #dc3545";

    // Other styles
    public static final String LINE_BELOW = "-fx-border-style: solid; -fx-border-width: 0 0 1px 0; -fx-border-color: #dee2e6;";
    public static final String SELECTED_ROW = "-fx-background-color: #4eb4eb;";
    public static final String DESELECTED_ROW = "-fx-background-color: #ffffff;";

    // Unicode characters
    public static final String FILTER_CHAR = "\u2200";
    public static final String UP_ARROW_CHAR = "\u2191";
    public static final String DOWN_ARROW_CHAR = "\u2193";
}
