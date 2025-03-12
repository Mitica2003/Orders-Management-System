package bll.validators;

import model.Orders;

/**
 * Clasa care verifica cantitatea unui produs in baza de date
 */
public class OrderQuantityValidator implements Validator<Orders> {
    /**
     * Functia de validare a cantitatii
     */
    @Override
    public void validate(Orders orders) {
        if(orders.getOrderQuantity() < 0)
            throw new IllegalArgumentException("The Order Quantity is not respected!");
    }
}
