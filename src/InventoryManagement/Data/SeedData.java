package InventoryManagement.Data;

import InventoryManagement.Models.InHouse;
import InventoryManagement.Models.Outsourced;
import InventoryManagement.Models.Part;

// Sadly, there's no such thing as assembly-private in Java
// (using internal modifier)
public class SeedData
{
    public static void initialize()
    {
        Inventory inventory = Inventory.getInstance();

        Part part1 = new InHouse(0, "Part 1", 5.00, 5, 12, 50, 1);
        Part part2 = new Outsourced(0, "New Part 2", 10.00, 10, 4, 20, "Outsource Inc.");
        Part part3 = new InHouse(0, "Random Part 1", 2.00, 12, 2, 15, 2);
        Part randomPart = new InHouse(0, "Random Object", 15.00, 1, 1, 10, 2);

        inventory.addPart(part1);
        inventory.addPart(part2);
        inventory.addPart(part3);
        inventory.addPart(randomPart);
    }
}
