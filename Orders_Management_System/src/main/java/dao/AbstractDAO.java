package dao;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import connection.ConnectionFactory;
import model.Orders;
import model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Clasa abstracta de acces pentru obiecte
 */
public class AbstractDAO<T> {
	protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());

	private final Class<T> type;

	/**
	 * Constructorul clasei
	 */
	@SuppressWarnings("unchecked")
	public AbstractDAO() {
		this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

	}

	private String createSelectQuery(String field) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		sb.append(" * ");
		sb.append(" FROM ");
		sb.append(type.getSimpleName());
		sb.append(" WHERE " + field + " =?");
		return sb.toString();
	}

	private String createInsertQuery(Field[] fields){
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ");
		sb.append(type.getSimpleName());
		sb.append(" (");

		for (int i = 0; i < fields.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(fields[i].getName());
		}

		sb.append(") VALUES (");

		for (int i = 0; i < fields.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append("?");
		}
		sb.append(")");

		return sb.toString();
	}

	private String createEditQuery(Field[] fields) {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ");
		sb.append(type.getSimpleName());
		sb.append(" SET ");
		for(Field field : fields){
			sb.append(field.getName());
			sb.append( " = ?,");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		sb.append(" WHERE id = ?");
		return sb.toString();
	}

	private List<T> createObjects(ResultSet resultSet) throws NoSuchMethodException {
		List<T> list = new ArrayList<T>();
		Constructor[] ctors = type.getDeclaredConstructors();
		Constructor ctor = null;

		for (int i = 0; i < ctors.length; i++) {
			ctor = ctors[i];
			if (ctor.getGenericParameterTypes().length == 0)
				break;
		}
		try {
			while (resultSet.next()) {
				ctor.setAccessible(true);
				T instance = (T)ctor.newInstance();

				for (Field field : type.getDeclaredFields()) {
					String fieldName = field.getName();
					Object value = resultSet.getObject(fieldName);
					PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fieldName, type);
					Method method = propertyDescriptor.getWriteMethod();
					method.invoke(instance, value);
				}
				list.add(instance);
			}
		} catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException |
				 InvocationTargetException | SQLException | IntrospectionException e) {
			e.printStackTrace();
		}
		return list;
	}

	private List<T> createProducts(ResultSet resultSet) {
		List<T> productList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				double price = resultSet.getDouble("price");
				int stock = resultSet.getInt("stock");

				Product product = new Product(id, name, price, stock);
				productList.add((T) product);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Error creating Product objects: " + e.getMessage());
		}
		return productList;
	}

	private List<T> createOrders(ResultSet resultSet) {
		List<T> orderList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				int clientId = resultSet.getInt("client_id");
				int productId = resultSet.getInt("product_id");
				int orderQuantity = resultSet.getInt("order_quantity");
				double orderPrice = resultSet.getDouble("order_price");

				// Assuming your Order class constructor takes these parameters in the same order
				Orders orders = new Orders(id, clientId, productId, orderQuantity, orderPrice);
				orderList.add((T) orders);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Error creating Order objects: " + e.getMessage());
		}
		return orderList;
	}

	/**
	 * Metoda de gasire a tuturor obiectelor al unui tip anume din baza de date
	 */
	public List<T> findAll() {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = "SELECT * FROM " + type.getSimpleName();
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			resultSet = statement.executeQuery();

			if(type.equals(Product.class))
				return createProducts(resultSet);
			else if(type.equals(Orders.class))
				return createOrders(resultSet);
			else
				return createObjects(resultSet);
		} catch (SQLException | NoSuchMethodException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:findAll " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}

	/**
	 * Metoda de gasire a unui obiect cu un anumit id din baza de date
	 */
	public T findById(int id) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = createSelectQuery("id");
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			resultSet = statement.executeQuery();

			return createObjects(resultSet).get(0);
		} catch (SQLException | NoSuchMethodException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:findById " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}

	/**
	 * Metoda de inserare a unui obiect in baza de date
	 */
	public T insert(T object) {
		Connection connection = null;
		PreparedStatement statement = null;
		String query = createInsertQuery(type.getDeclaredFields());

		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);

			Field[] fields = type.getDeclaredFields();

			int parameterIndex = 1;
			for (Field field : fields) {
				if (!field.isSynthetic() && !Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
					field.setAccessible(true);
					Object value = field.get(object);
					statement.setObject(parameterIndex++, field.get(object));
				}
			}

			statement.executeUpdate();
		} catch (SQLException | IllegalAccessException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:insert " + e.getMessage());
		} finally {
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}

		return null;
	}

	/**
	 * Metoda de editare a unui obiect in baza de date
	 */
	public T update(T object) {
		Connection connection = null;
		PreparedStatement statement = null;

		// Create the SQL query
		String query = createEditQuery(type.getDeclaredFields());

		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);

			int index = 1;
			Field[] fields = type.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = field.get(object);
				statement.setObject(index, value);
				index++;
			}

			statement.setInt(index, (Integer) fields[0].get(object));
			statement.executeUpdate();
		} catch (SQLException | IllegalAccessException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:update " + e.getMessage());
		} finally {
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}

	/**
	 * Metoda de stergere a unui obiect in baza de date
	 */
	public T delete(T object) {
		Connection connection = null;
		PreparedStatement statement = null;

		String query = "DELETE FROM " + type.getSimpleName() + " WHERE id = ?";

		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);

			Field field = type.getDeclaredFields()[0];
			field.setAccessible(true);
			statement.setInt(1,(Integer) field.get(object));

			statement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:delete " + e.getMessage());
		} catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}

	/**
	 * Metoda de vizualizare a tuturor obiectelor dintr-un tabel din baza de date
	 */
	public JTable viewListOfFields(JTable table, List<T> objectList){
		DefaultTableModel tableModel = new DefaultTableModel();

		Field[] fields = type.getDeclaredFields();
		String[] columnName = new String[fields.length];

		for (int i = 0; i < fields.length; i++) {
			columnName[i] = fields[i].getName();
		}


		for(Field field: fields){
			tableModel.addColumn(field.getName());
		}

		tableModel.addRow(columnName);

		for(T list: objectList){
			Object[] dataToAdd = new Object[fields.length];
			for( int i = 0; i < fields.length; i++){
				try {
					Field field = fields[i];
					field.setAccessible(true);
					Object value = field.get(list);
					dataToAdd[i] = value;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			tableModel.addRow(dataToAdd);
		}
		table.setModel(tableModel);

		return table;
	}

}
