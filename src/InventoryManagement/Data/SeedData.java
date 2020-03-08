package InventoryManagement.Data;

import InventoryManagement.Models.InHouse;
import InventoryManagement.Models.Outsourced;
import InventoryManagement.Models.Part;
import InventoryManagement.Models.Product;

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

        Product product1 = new Product(0, "Product 1", 1.00, 4, 1, 20);
        Product product2 = new Product(0, "Product 2", 2.00, 5, 1, 20);
        Product product3 = new Product(0, "Product 3", 3.00, 6, 1, 20);
        Product product4 = new Product(0, "Random Product", 4.00, 7, 1, 20);

        inventory.addProduct(product1);
        inventory.addProduct(product2);
        inventory.addProduct(product3);
        inventory.addProduct(product4);
    }
}
