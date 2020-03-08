package InventoryManagement.Data;

import InventoryManagement.Models.InHouse;
import InventoryManagement.Models.Outsourced;
import InventoryManagement.Models.Part;
import InventoryManagement.Models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.stream.Collectors;

// Uses singleton implementation to mimic dbcontext in .NET
// and so all views can have access to it
// TODO: Implement authentication and authorization
public class Inventory
{
    private ObservableList<Part> allParts;
    private ObservableList<Product> allProducts;

    // For auto_increment ids
    private int partCounter = 1;
    private int productCounter = 1;

    /* CRUD implementation below */

    // Parts

    public Part lookupPart(int partId)
    {
        // LINQ-like search
        return allParts.stream()
                       .filter(part -> part.getId() == partId)
                       .findFirst()
                       .orElse(null);
    }

    public ObservableList<Part> lookupPart(String partName)
    {
        return allParts.stream()
                       .filter(part -> part.getName()
                                           .toLowerCase()
                                           .contains(partName.toLowerCase()))
                       .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public ObservableList<Part> getAllParts()
    {
        return allParts;
    }

    public void addPart(Part newPart)
    {
        internalAddPart(partCounter++, newPart);
    }

    private void internalAddPart(int partId, Part newPart)
    {
        newPart.setId(partId);
        allParts.add(newPart);
    }

    public void updatePart(int index, Part selectedPart)
    {

    }

    // TODO: Ask professor if I should implement max > min
    // TODO: Implement validation
    public void updatePartById(int partId, Part selectedPart)
    {
        Part part = lookupPart(partId);
        if (part == null)
            throw new NullPointerException("Cannot find the part of id'" + partId + "'.");

        // TODO: Implement changing type
        if (part instanceof InHouse &&
                selectedPart instanceof Outsourced)
        {
            deletePart(part);
            Outsourced newPart = new Outsourced(
                    partId,
                    selectedPart.getName(),
                    selectedPart.getPrice(),
                    selectedPart.getStock(),
                    selectedPart.getMin(),
                    selectedPart.getMax(),
                    ((Outsourced) selectedPart).getCompanyName()
            );
            internalAddPart(newPart.getId(), newPart);
        }
        else if (part instanceof Outsourced &&
                selectedPart instanceof InHouse)
        {
            deletePart(part);
            InHouse newPart = new InHouse(
                    partId,
                    selectedPart.getName(),
                    selectedPart.getPrice(),
                    selectedPart.getStock(),
                    selectedPart.getMin(),
                    selectedPart.getMax(),
                    ((InHouse) selectedPart).getMachineId()
            );
            internalAddPart(newPart.getId(), newPart);
        }
        else
        {
            // id shouldn't be changeable after creating part
            part.setName(selectedPart.getName());
            part.setPrice(selectedPart.getPrice());
            part.setStock(selectedPart.getStock());
            part.setMin(selectedPart.getMin());
            part.setMax(selectedPart.getMax());

            if (part instanceof InHouse)
                ((InHouse) part).setMachineId(((InHouse) selectedPart).getMachineId());
            else if (part instanceof Outsourced)
                ((Outsourced) part).setCompanyName(((Outsourced) selectedPart).getCompanyName());
        }
    }

    public boolean deletePart(Part selectedPart)
    {
        return allParts.remove(selectedPart);
    }

    public boolean deletePartById(int partId)
    {
        Part selectedPart = lookupPart(partId);
        if (selectedPart != null)
            return deletePart(selectedPart);

        return false;
    }

    // Products

    public Product lookupProduct(int productId)
    {
        // LINQ-like search
        return allProducts.stream()
                          .filter(product -> product.getId() == productId)
                          .findFirst()
                          .orElse(null);
    }

    public ObservableList<Product> lookupProduct(String productName)
    {
        return allProducts.stream()
                          .filter(product -> product.getName()
                                                    .toLowerCase()
                                                    .contains(productName.toLowerCase()))
                          .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public ObservableList<Product> getAllProducts()
    {
        return allProducts;
    }

    public void addProduct(Product newProduct)
    {
        internalAddProduct(productCounter++, newProduct);
    }

    private void internalAddProduct(int productId, Product newProduct)
    {
        newProduct.setId(productId);
        allProducts.add(newProduct);
    }

    public void updateProduct(int index, Product newProduct)
    {

    }

    // TODO: Implement validation
    public void updateProductById(int productId, Product newProduct)
    {
        Product product = lookupProduct(productId);
        if (product == null)
            throw new NullPointerException("Cannot find the product of id'" + productId + "'.");

        product.setName(newProduct.getName());
        product.setPrice(newProduct.getPrice());
        product.setStock(newProduct.getStock());
        product.setMin(newProduct.getMin());
        product.setMax(newProduct.getMax());
    }

    public boolean deleteProduct(Product selectedProduct)
    {
        return allProducts.remove(selectedProduct);
    }

    public boolean deleteProductById(int productId)
    {
        Product product = lookupProduct(productId);
        if (product != null)
            return deleteProduct(product);

        return false;
    }

    /* Singleton implementation below */

    private static Inventory instance = null;

    // This prevents instantiation of the class outside of this class
    private Inventory()
    {
        allParts = FXCollections.observableArrayList();
        allProducts = FXCollections.observableArrayList();
    }

    public static Inventory getInstance()
    {
        if (instance == null)
            instance = new Inventory();

        return instance;
    }
}
