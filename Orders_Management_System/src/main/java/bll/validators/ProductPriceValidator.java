package bll.validators;

import model.Product;

/**
 * Clasa care verifica pretul unui produs introdus in interfata de catre utilizator
 */
public class ProductPriceValidator implements Validator<Product>{

    /**
     * Functia de validare a pretului
     */
    @Override
    public void validate(Product product) {
        if( product.getPrice() < 0 || product.getPrice() > 2500 )
            throw new IllegalArgumentException("The Product Price limit is not respected!");
    }
}
