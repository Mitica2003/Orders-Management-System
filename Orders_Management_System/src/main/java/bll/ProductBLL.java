package bll;

import dao.ProductDAO;
import model.Product;

import bll.validators.Validator;
import bll.validators.ProductPriceValidator;
import bll.validators.ProductStockValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Clasa pentru gestionarea produsului in baza de date
 */
public class ProductBLL {
    private List<Validator<Product>> validators;
    private ProductDAO productDAO;

    /**
     * Constructorul clasei
     */
    public ProductBLL() {
        validators = new ArrayList<Validator<Product>>();
        validators.add(new ProductPriceValidator());
        validators.add(new ProductStockValidator());

        productDAO = new ProductDAO();
    }

    /**
     * Metoda pentru inserare a produsului in baza de date, dupa validari
     */
    public Product insertProduct(Product product){
        for(Validator<Product> productValidator: validators)
            productValidator.validate(product);

        return productDAO.insert(product);
    }

    /**
     * Metoda pentru editare a produsului in baza de date, dupa validari
     */
    public Product editProduct(Product product){
        for(Validator<Product> productValidator: validators)
            productValidator.validate(product);

        return productDAO.update(product);
    }

    /**
     * Metoda pentru stergere a produsului in baza de date, dupa validari
     */
    public Product deleteProduct(Product product){
        for(Validator<Product> productValidator: validators)
            productValidator.validate(product);

        return productDAO.delete(product);
    }

    /**
     * Metoda pentru gasire a tuturor produselor in baza de date
     */
    public List<Product> findAllProducts(){
        return productDAO.findAll();
    }

    /**
     * Metoda pentru gasirea unui produs in functie de id-ul sau in baza de date
     */
    public Product findProductById(int id) {
        Product st = productDAO.findById(id);
        if (st == null) {
            throw new NoSuchElementException("The product with id =" + id + " was not found!");
        }
        return st;
    }
}
