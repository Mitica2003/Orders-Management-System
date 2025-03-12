package dao;

import model.Client;

import javax.swing.*;
import java.util.List;

/**
 * Clasa de acces a datelor unui client
 */
public class ClientDAO extends AbstractDAO<Client> {

    /**
     * Constructorul clasei
     */
    public ClientDAO() {
        super();
    }

    /**
     * Metoda de gasire a tuturor clientilor din baza de date
     */
    @Override
    public List<Client> findAll() {
        return super.findAll();
    }

    /**
     * Metoda de gasire a unui client cu un anumit id din baza de date
     */
    @Override
    public Client findById(int id) {
        return super.findById(id);
    }

    /**
     * Metoda de inserare a unui client in baza de date
     */
    @Override
    public Client insert(Client object) {
        return super.insert(object);
    }

    /**
     * Metoda de editare a unui client in baza de date
     */
    @Override
    public Client update(Client object) {
        return super.update(object);
    }

    /**
     * Metoda de stergere a unui client in baza de date
     */
    @Override
    public Client delete(Client object) {
        return super.delete(object);
    }

    /**
     * Metoda de vizualizare a clientilor in baza de date
     */
    @Override
    public JTable viewListOfFields(JTable table, List<Client> objectList) {
        return super.viewListOfFields(table, objectList);
    }
}
