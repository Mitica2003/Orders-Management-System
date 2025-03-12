package bll;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import bll.validators.EmailValidator;
import bll.validators.ClientAgeValidator;
import bll.validators.Validator;
import dao.ClientDAO;
import model.Client;

/**
 * Clasa pentru gestionarea clientului in baza de date
 */
public class ClientBLL {

	private List<Validator<Client>> validators;
	private ClientDAO clientDAO;

	/**
	 * Constructorul clasei
	 */
	public ClientBLL() {
		validators = new ArrayList<Validator<Client>>();
		validators.add(new EmailValidator());
		validators.add(new ClientAgeValidator());

		clientDAO = new ClientDAO();
	}

	/**
	 * Metoda pentru inserare a clientului in baza de date, dupa validari
	 */
	public Client insertClient(Client client){
		for(Validator<Client> clientValidator: validators)
			clientValidator.validate(client);

		return clientDAO.insert(client);
	}

	/**
	 * Metoda pentru editare a clientului in baza de date, dupa validari
	 */
	public Client editClient(Client client){
		for(Validator<Client> clientValidator: validators)
			clientValidator.validate(client);

		return clientDAO.update(client);
	}

	/**
	 * Metoda pentru stergere a clientului in baza de date, dupa validari
	 */
	public Client deleteClient(Client client){
		for(Validator<Client> clientValidator: validators)
			clientValidator.validate(client);

		return clientDAO.delete(client);
	}

	/**
	 * Metoda pentru gasire a tuturor clientilor in baza de date
	 */
	public List<Client> findAllClients(){
		return clientDAO.findAll();
	}

	/**
	 * Metoda pentru gasirea unui client in functie de id-ul sau in baza de date
	 */
	public Client findClientById(int id) {
		Client st = clientDAO.findById(id);
		if (st == null) {
			throw new NoSuchElementException("The student with id =" + id + " was not found!");
		}
		return st;
	}

}
