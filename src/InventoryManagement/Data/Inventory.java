package InventoryManagement.Data;

import InventoryManagement.Models.InHouse;
import InventoryManagement.Models.Outsourced;
import InventoryManagement.Models.Part;
import InventoryManagement.Models.Product;
import InventoryManagement.Utils.Enums;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.stream.Collectors;

// Uses singleton implementation to mimic dbcontext in .NET
// and so all views can have access to it
public class Inventory
{
    // Supposed to be a struct, so only getter methods
    private class EntityEntry<TEntity>
    {
        private TEntity entity;
        private TEntity updatedEntity;
        private Enums.EntityState state;

        public EntityEntry(TEntity entity, TEntity updatedEntity, Enums.EntityState state)
        {
            this.entity = entity;
            this.updatedEntity = updatedEntity;
            this.state = state;
        }

        public TEntity getEntity()
        {
            return entity;
        }

        public TEntity getUpdatedEntity()
        {
            return updatedEntity;
        }

        public Enums.EntityState getState()
        {
            return state;
        }
    }

    private ObservableList<Part> allParts;
    private ObservableList<Product> allProducts;

    // Tracks the changes to the lists above, which could be
    // applied later in the code using saveChanges()
    private ArrayList<EntityEntry<Part>> trackedParts;
    private ArrayList<EntityEntry<Product>> trackedProducts;

    // For auto_increment ids
    private int partCounter = 1;
    private int productCounter = 1;

    // Save all the tracked changes
    public void saveChanges()
    {
        for (EntityEntry<Part> entry : trackedParts)
        {
            Enums.EntityState state = entry.state;

            if (state == Enums.EntityState.Added)
                allParts.add(entry.entity);
            else if (state == Enums.EntityState.Modified)
            {
                Part part = entry.entity;
                Part selectedPart = entry.updatedEntity;

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
            else if (state == Enums.EntityState.Deleted)
                allParts.remove(entry.entity);
        }

        for (EntityEntry<Product> entry : trackedProducts)
        {
            Enums.EntityState state = entry.state;

            if (state == Enums.EntityState.Added)
                allProducts.add(entry.entity);
            else if (state == Enums.EntityState.Modified)
            {
                Product product = entry.entity;
                Product newProduct = entry.updatedEntity;

                product.setName(newProduct.getName());
                product.setPrice(newProduct.getPrice());
                product.setStock(newProduct.getStock());
                product.setMin(newProduct.getMin());
                product.setMax(newProduct.getMax());

                product.clearAllAssociatedParts();
                for (Part part : newProduct.getAllAssociatedParts())
                    product.addAssociatedPart(part);
            }
            else if (state == Enums.EntityState.Deleted)
                allProducts.remove(entry.entity);
        }

        trackedParts.clear();
        trackedProducts.clear();
    }

    public void cancelChanges()
    {
        trackedParts.clear();
        trackedProducts.clear();
    }

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

    public void addPart(Part newPart, boolean sameId)
    {
        if (sameId)
            internalAddPart(newPart.getId(), newPart);
        else
            addPart(newPart);
    }

    private void internalAddPart(int partId, Part newPart)
    {
        newPart.setId(partId);
        trackedParts.add(new EntityEntry<>(newPart, null, Enums.EntityState.Added));
    }

    public void updatePart(int index, Part selectedPart)
    {

    }

    // TODO: Ask professor if I should implement max > min
    public void updatePartById(int partId, Part selectedPart)
    {
        Part part = lookupPart(partId);
        if (part == null)
            throw new NullPointerException("Cannot find the part of id '" + partId + "'.");

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
            trackedParts.add(new EntityEntry<>(part, selectedPart, Enums.EntityState.Modified));
        }
    }

    public boolean deletePart(Part selectedPart)
    {
        trackedParts.add(new EntityEntry<>(selectedPart, null, Enums.EntityState.Deleted));
        return true;
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
        trackedProducts.add(new EntityEntry<>(newProduct, null, Enums.EntityState.Added));
    }

    public void updateProduct(int index, Product newProduct)
    {

    }

    public void updateProductById(int productId, Product newProduct)
    {
        Product product = lookupProduct(productId);
        if (product == null)
            throw new NullPointerException("Cannot find the product of id '" + productId + "'.");

        trackedProducts.add(new EntityEntry<>(product, newProduct, Enums.EntityState.Modified));
    }

    public boolean deleteProduct(Product selectedProduct)
    {
        trackedProducts.add(new EntityEntry<>(selectedProduct, null, Enums.EntityState.Deleted));
        return true;
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

        trackedParts = new ArrayList<>();
        trackedProducts = new ArrayList<>();
    }

    public static Inventory getInstance()
    {
        if (instance == null)
            instance = new Inventory();

        return instance;
    }
}
