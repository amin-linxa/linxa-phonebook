package com.linxa.phonebook.data.provider;

import com.linxa.phonebook.data.entity.Contact;
import com.linxa.phonebook.properties.DbProperties;
import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class ContactDataProvider extends AbstractBackEndDataProvider<Contact, CrudFilter> {

    private static final String SQL_INSERT =
        "     insert into contact (first_name, last_name, email, phone_number, country, city, street)" +
            " values (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
        "     update contact" +
            " set first_name = ?, last_name = ?, email = ?, phone_number = ?, country = ?, city = ?, street = ?" +
            " where id = ?";

    private static final String SQL_DELETE = "delete from contact where id = ?";

    private static final Map<String, String> MAP_FIELD_TO_COLUMN = Map.of(
        "firstName", "first_name",
        "lastName", "last_name",
        "email", "email",
        "phoneNumber", "phone_number");

    public boolean isUniquePhoneNumber(Long currentContactId, String phoneNumber) {
        var dbProperties = DbProperties.getInstance();
        try (var connection = DriverManager.getConnection(dbProperties.getConnectionUrl(), dbProperties.getUsername(), dbProperties.getPassword())) {
            var whereClause = String.format(" where phone_number = '%s'", phoneNumber);
            if (Objects.nonNull(currentContactId))
                whereClause += String.format(" and id <> %d", currentContactId);
            var statement = connection.prepareStatement("select count(id) = 0 from contact " + whereClause);
            var resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void persist(Contact item) {
        var dbProperties = DbProperties.getInstance();
        try (var connection = DriverManager.getConnection(dbProperties.getConnectionUrl(), dbProperties.getUsername(), dbProperties.getPassword())) {
            var id = item.getId();
            var statement = Objects.isNull(id) ? connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS) : connection.prepareStatement(SQL_UPDATE);
            statement.setString(1, item.getFirstName());
            statement.setString(2, item.getLastName());
            statement.setString(3, item.getEmail());
            statement.setString(4, item.getPhoneNumber());
            statement.setString(5, item.getCountry());
            statement.setString(6, item.getCity());
            statement.setString(7, item.getStreet());
            if (Objects.nonNull(id))
                statement.setLong(8, item.getId());
            statement.executeUpdate();
            if (Objects.isNull(id)) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next())
                        item.setId(generatedKeys.getLong(1));
                    else
                        throw new SQLException("Creating contact failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Contact item) {
        var dbProperties = DbProperties.getInstance();
        try (var connection = DriverManager.getConnection(dbProperties.getConnectionUrl(), dbProperties.getUsername(), dbProperties.getPassword())) {
            var statement = connection.prepareStatement(SQL_DELETE);
            statement.setLong(1, item.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Stream<Contact> fetchFromBackEnd(Query<Contact, CrudFilter> query) {
        var dbProperties = DbProperties.getInstance();
        try (var connection = DriverManager.getConnection(dbProperties.getConnectionUrl(), dbProperties.getUsername(), dbProperties.getPassword())) {
            var whereClause = query.getFilter().isPresent() ? fetchWhereClause(query.getFilter().get()) : "";
            var orderClause = query.getFilter().isPresent() ? fetchOrderClause(query.getFilter().get()) : "";
            var statement = connection.prepareStatement("select * from contact " + whereClause + " " + orderClause + " limit ?, ?");
            statement.setInt(1, query.getOffset());
            statement.setInt(2, query.getLimit());
            var resultSet = statement.executeQuery();
            var contacts = new ArrayList<Contact>();
            while (resultSet.next())
                contacts.add(extractContact(resultSet));
            return contacts.stream();
        } catch (SQLException e) {
            e.printStackTrace();
            return Stream.empty();
        }
    }

    @Override
    protected int sizeInBackEnd(Query<Contact, CrudFilter> query) {
        var dbProperties = DbProperties.getInstance();
        try (var connection = DriverManager.getConnection(dbProperties.getConnectionUrl(), dbProperties.getUsername(), dbProperties.getPassword())) {
            var whereClause = query.getFilter().isPresent() ? fetchWhereClause(query.getFilter().get()) : "";
            var orderClause = query.getFilter().isPresent() ? fetchOrderClause(query.getFilter().get()) : "";
            var statement = connection.prepareStatement("select count(id) as count from contact " + whereClause + " " + orderClause + " limit ?, ?");
            statement.setInt(1, query.getOffset());
            statement.setInt(2, query.getLimit());
            var resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private String fetchWhereClause(CrudFilter filter) {
        if (filter.getConstraints().isEmpty())
            return "";
        StringBuilder where = new StringBuilder(" where ");
        filter.getConstraints().forEach((field, term) ->
            where.append("lower(").append(MAP_FIELD_TO_COLUMN.get(field)).append(")")
                .append(" like lower('%").append(term.replaceAll("%", "\\\\%")).append("%')")
                .append(" and "));
        return where.delete(where.length() - 5, where.length()).toString();
    }

    private String fetchOrderClause(CrudFilter filter) {
        if (filter.getSortOrders().isEmpty())
            return "";
        StringBuilder order = new StringBuilder(" order by ");
        filter.getSortOrders().forEach((field, direction) ->
            order.append(MAP_FIELD_TO_COLUMN.get(field)).append(" ")
                .append(Objects.equals(direction, SortDirection.ASCENDING) ? "ASC" : "DESC")
                .append(", "));
        return order.delete(order.length() - 2, order.length()).toString();
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
