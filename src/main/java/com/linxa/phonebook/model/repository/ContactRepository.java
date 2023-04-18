package com.linxa.phonebook.model.repository;

import com.linxa.phonebook.model.entity.Contact;
import com.linxa.phonebook.properties.DbProperties;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ContactRepository {

    private static final String SQL_FIND_ALL = "select * from contact";

    private static final String SQL_SEARCH =
        "     select * from contact" +
            " where lower(first_name) like lower(concat('%', ?, '%'))" +
            "   or lower(last_name) like lower(concat('%', ?, '%'))";

    private static final String SQL_FIND_ONE = "select * from contact where id = ?";

    private static final String SQL_INSERT =
        "     insert into contact (first_name, last_name, email, phone_number, country, city, street)" +
            " values (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
        "     update contact" +
            " set first_name = ?, last_name = ?, email = ?, phone_number = ?, country = ?, city = ?, street = ?" +
            " where id = ?";

    private static final String SQL_DELETE = "delete from contact where id = ?";

    private static final ContactRepository INSTANCE = new ContactRepository();

    private ContactRepository() {
    }

    public static ContactRepository getInstance() {
        return INSTANCE;
    }

    public List<Contact> findAll() {
        var dbProperties = DbProperties.getInstance();
        try (var connection = DriverManager.getConnection(dbProperties.getConnectionUrl(), dbProperties.getUsername(), dbProperties.getPassword())) {
            var statement = connection.prepareStatement(SQL_FIND_ALL);
            var resultSet = statement.executeQuery();
            var contacts = new ArrayList<Contact>();
            while (resultSet.next())
                contacts.add(extractContact(resultSet));
            return contacts;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Contact findOne(Long id) {
        var dbProperties = DbProperties.getInstance();
        try (var connection = DriverManager.getConnection(dbProperties.getConnectionUrl(), dbProperties.getUsername(), dbProperties.getPassword())) {
            var statement = connection.prepareStatement(SQL_FIND_ONE);
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();
            resultSet.next();
            return extractContact(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Contact> search(String term) {
        var dbProperties = DbProperties.getInstance();
        try (var connection = DriverManager.getConnection(dbProperties.getConnectionUrl(), dbProperties.getUsername(), dbProperties.getPassword())) {
            var statement = connection.prepareStatement(SQL_SEARCH);
            statement.setString(1, term);
            statement.setString(2, term);
            var resultSet = statement.executeQuery();
            var contacts = new ArrayList<Contact>();
            while (resultSet.next())
                contacts.add(extractContact(resultSet));
            return contacts;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void insert(Contact contact) {
        var dbProperties = DbProperties.getInstance();
        try (var connection = DriverManager.getConnection(dbProperties.getConnectionUrl(), dbProperties.getUsername(), dbProperties.getPassword())) {
            var statement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, contact.getFirstName());
            statement.setString(2, contact.getLastName());
            statement.setString(3, contact.getEmail());
            statement.setString(4, contact.getPhoneNumber());
            statement.setString(5, contact.getCountry());
            statement.setString(6, contact.getCity());
            statement.setString(7, contact.getStreet());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    contact.setId(generatedKeys.getLong(1));
                }
                else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Contact contact) {
        var dbProperties = DbProperties.getInstance();
        try (var connection = DriverManager.getConnection(dbProperties.getConnectionUrl(), dbProperties.getUsername(), dbProperties.getPassword())) {
            var statement = connection.prepareStatement(SQL_UPDATE);
            statement.setString(1, contact.getFirstName());
            statement.setString(2, contact.getLastName());
            statement.setString(3, contact.getEmail());
            statement.setString(4, contact.getPhoneNumber());
            statement.setString(5, contact.getCountry());
            statement.setString(6, contact.getCity());
            statement.setString(7, contact.getStreet());
            statement.setLong(8, contact.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Contact contact) {
        var dbProperties = DbProperties.getInstance();
        try (var connection = DriverManager.getConnection(dbProperties.getConnectionUrl(), dbProperties.getUsername(), dbProperties.getPassword())) {
            var statement = connection.prepareStatement(SQL_DELETE);
            statement.setLong(1, contact.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Contact extractContact(ResultSet resultSet) throws SQLException {
        return new Contact(
            resultSet.getLong("id"),
            resultSet.getString("first_name"),
            resultSet.getString("last_name"),
            resultSet.getString("email"),
            resultSet.getString("phone_number"),
            resultSet.getString("country"),
            resultSet.getString("city"),
            resultSet.getString("street")
        );
    }

}
