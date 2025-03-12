package bll.validators;

/**
 * Interfata care restituie functia de validare pentru fiecare clasa in parte(client, product si order)
 */
public interface Validator<T> {

	/**
	 * Functia de validare
	 */
	public void validate(T t);
}
