package org.liliya.hotelapp.service;

import org.liliya.hotelapp.configuration.Configuration;
import org.liliya.hotelapp.model.Apartment;
import org.liliya.hotelapp.model.Client;
import org.liliya.hotelapp.model.ReservationStatus;
import org.liliya.hotelapp.persistence.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ApartmentServiceImpl implements ApartmentService {
    private final Configuration configuration;
    private final DatabaseConnection databaseConnection;

    public ApartmentServiceImpl(DatabaseConnection databaseConnection) {
        this.configuration = Configuration.getInstance();
        this.databaseConnection = databaseConnection;
    }

    private static final String INSERT_APARTMENT_QUERY = "INSERT INTO apartments (price, status) VALUES (?, ?)";
    private static final String INSERT_CLIENT_QUERY = "INSERT INTO clients (name) VALUES (?)";
    private static final String FIND_AVAILABLE_APARTMENT_QUERY = "SELECT id FROM apartments WHERE status = ? LIMIT 1";
    private static final String UPDATE_APARTMENT_AFTER_RESERVE_QUERY = "UPDATE apartments SET status = ?, client_id = ? WHERE id = ?";
    private static final String FIND_CLIENT_QUERY = "SELECT client_id FROM apartments WHERE id = ?";
    private static final String DELETE_CLIENT_QUERY = "DELETE FROM clients WHERE id = ?";
    private static final String UPDATE_APARTMENT_AFTER_RELEASE_QUERY = "UPDATE apartments SET status = ?, client_id = NULL WHERE id = ?";
    private static final String GET_PAGINATED_SORTED_APARTMENTS_QUERY = """
                SELECT a.id, a.price, a.status, c.id AS client_id, c.name AS client_name
                FROM apartments a
                LEFT JOIN clients c ON a.client_id = c.id
                ORDER BY %s
                LIMIT ? OFFSET ?
            """;

    @Override
    public int register(double price) {
        int generatedId = -1;
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_APARTMENT_QUERY, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            statement.setDouble(1, price);
            statement.setString(2, ReservationStatus.AVAILABLE.name());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getInt(1);
                        System.out.println("Apartment registered with ID: " + generatedId);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedId;
    }

    @Override
    public boolean reserve(Client client) {
        if (!configuration.statusChangeAvailability()) {
            return false;
        }
        try (Connection connection = databaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            int clientId = insertClient(connection, client);
            if (clientId == -1) {
                connection.rollback();
                return false;
            }
            int apartmentId = findAvailableApartment(connection);
            if (apartmentId == -1) {
                connection.rollback();
                return false;
            }
            if (updateApartmentAfterReserve(connection, apartmentId, clientId)) {
                connection.commit();
                return true;
            } else {
                connection.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean release(int id) {
        if (!configuration.statusChangeAvailability()) {
            return false;
        }
        try (Connection connection = databaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            Integer clientId = findClientByApartmentId(connection, id);
            if (clientId != null) {
                deleteClient(connection, clientId);
            }
            if (updateApartmentAfterRelease(connection, id)) {
                connection.commit();
                return true;
            } else {
                connection.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public List<Apartment> getPaginatedAndSortedApartments(int page, int size, String sortBy) {
        List<Apartment> apartments = new ArrayList<>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_PAGINATED_SORTED_APARTMENTS_QUERY.formatted(sortBy))) {

            int offset = (page - 1) * size;

            statement.setInt(1, size);
            statement.setInt(2, offset);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Client client = null;
                    int clientId = resultSet.getInt("client_id");
                    if (clientId > 0) {
                        client = new Client(clientId, resultSet.getString("client_name"));
                    }
                    Apartment apartment = new Apartment(
                            resultSet.getInt("id"),
                            resultSet.getDouble("price"),
                            ReservationStatus.valueOf(resultSet.getString("status")),
                            client
                    );
                    apartments.add(apartment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return apartments;
    }

    private int insertClient(Connection connection, Client client) throws SQLException {
        try (PreparedStatement insertClientStatement = connection.
                prepareStatement(INSERT_CLIENT_QUERY, PreparedStatement.RETURN_GENERATED_KEYS)) {
            insertClientStatement.setString(1, client.getName());
            insertClientStatement.executeUpdate();

            try (ResultSet generatedKeys = insertClientStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1;
    }


    private int findAvailableApartment(Connection connection) throws SQLException {
        try (PreparedStatement findStatement = connection.prepareStatement(FIND_AVAILABLE_APARTMENT_QUERY)) {
            findStatement.setString(1, ReservationStatus.AVAILABLE.name());
            try (ResultSet resultSet = findStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }
        return -1;
    }

    private boolean updateApartmentAfterReserve(Connection connection, int apartmentId, int clientId) throws SQLException {
        try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE_APARTMENT_AFTER_RESERVE_QUERY)) {
            updateStatement.setString(1, ReservationStatus.RESERVED.name());
            updateStatement.setInt(2, clientId);
            updateStatement.setInt(3, apartmentId);
            return updateStatement.executeUpdate() > 0;
        }
    }

    private boolean updateApartmentAfterRelease(Connection connection, int apartmentId) throws SQLException {
        try (PreparedStatement updateApartmentStatement = connection.prepareStatement(UPDATE_APARTMENT_AFTER_RELEASE_QUERY)) {
            updateApartmentStatement.setString(1, ReservationStatus.AVAILABLE.name());
            updateApartmentStatement.setInt(2, apartmentId);
            return updateApartmentStatement.executeUpdate() > 0;
        }
    }


    private Integer findClientByApartmentId(Connection connection, int apartmentId) throws SQLException {
        try (PreparedStatement findClientStatement = connection.prepareStatement(FIND_CLIENT_QUERY)) {
            findClientStatement.setInt(1, apartmentId);
            try (ResultSet resultSet = findClientStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("client_id");
                }
            }
        }
        return null;
    }

    private void deleteClient(Connection connection, int clientId) throws SQLException {
        try (PreparedStatement deleteClientStatement = connection.prepareStatement(DELETE_CLIENT_QUERY)) {
            deleteClientStatement.setInt(1, clientId);
            deleteClientStatement.executeUpdate();
        }
    }

}