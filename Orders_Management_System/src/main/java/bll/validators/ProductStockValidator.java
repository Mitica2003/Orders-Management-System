package bll.validators;

import model.Product;

import javax.swing.*;

/**
 * Clasa care verifica stocul unui produs din baza de date
 */
public class ProductStockValidator implements Validator<Product>{
    /**
     * Functia de validare a stocului
     */
    @Override
    public void validate(Product product) {
        if( product.getStock() < 0 ) {
            throw new IllegalArgumentException("The product is in under-stock!");
        }
    }
}
