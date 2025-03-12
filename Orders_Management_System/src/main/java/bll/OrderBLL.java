package bll;

import bll.validators.OrderQuantityValidator;
import bll.validators.Validator;
import dao.OrderDAO;
import model.Orders;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Clasa pentru gestionarea comenzii in baza de date
 */
public class OrderBLL {

    private List<Validator<Orders>> validators;
    private OrderDAO orderDAO;

    /**
     * Constructorul clasei
     */
    public OrderBLL() {
        validators = new ArrayList<Validator<Orders>>();
        validators.add(new OrderQuantityValidator());

        orderDAO = new OrderDAO();
    }

    /**
     * Metoda pentru inserare a comenzii in baza de date, dupa validari
     */
    public Orders insertOrder(Orders orders){
        for(Validator<Orders> orderValidator: validators)
            orderValidator.validate(orders);

        return orderDAO.insert(orders);
    }

    /**
     * Metoda pentru editare a comenzii in baza de date, dupa validari
     */
    public Orders editOrder(Orders orders){
        for(Validator<Orders> orderValidator: validators)
            orderValidator.validate(orders);

        return orderDAO.update(orders);
    }

    /**
     * Metoda pentru stergere a comenzii in baza de date, dupa validari
     */
    public Orders deleteOrder(Orders orders){
        for(Validator<Orders> orderValidator: validators)
            orderValidator.validate(orders);

        return orderDAO.delete(orders);
    }

    /**
     * Metoda pentru gasire a tuturor comenzilor in baza de date
     */
    public List<Orders> findAllOrders(){
        return orderDAO.findAll();
    }

    /**
     * Metoda pentru gasirea unei comenzi in functie de id-ul sau in baza de date
     */
    public Orders findOrderById(int id) {
        Orders st = orderDAO.findById(id);
        if (st == null) {
            throw new NoSuchElementException("The order with id =" + id + " was not found!");
        }
        return st;
    }


}
