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

    @Override
    public int register(double price) {
        String query = "INSERT INTO apartments (price, status) VALUES (?, ?)";
        int generatedId = -1;
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)
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
        String insertClientQuery = "INSERT INTO clients (name) VALUES (?)";
        String findAvailableQuery = "SELECT id FROM apartments WHERE status = ? LIMIT 1";
        String updateApartmentQuery = "UPDATE apartments SET status = ?, client_id = ? WHERE id = ?";


        try (Connection connection = databaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            int clientId = -1;
            try (PreparedStatement insertClientStatement = connection.prepareStatement(insertClientQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                insertClientStatement.setString(1, client.getName());
                insertClientStatement.executeUpdate();

                try (ResultSet generatedKeys = insertClientStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        clientId = generatedKeys.getInt(1);
                    }
                }
            }

            int apartmentId = -1;
            try (PreparedStatement findStatement = connection.prepareStatement(findAvailableQuery)) {
                findStatement.setString(1, ReservationStatus.AVAILABLE.name());
                try (ResultSet resultSet = findStatement.executeQuery()) {
                    if (resultSet.next()) {
                        apartmentId = resultSet.getInt("id");
                    }
                }
            }

            if (apartmentId == -1) {
                connection.rollback();
                return false;
            }

            try (PreparedStatement updateStatement = connection.prepareStatement(updateApartmentQuery)) {
                updateStatement.setString(1, ReservationStatus.RESERVED.name());
                updateStatement.setInt(2, clientId);
                updateStatement.setInt(3, apartmentId);
                if (updateStatement.executeUpdate() > 0) {
                    connection.commit();
                    return true;
                }
            }

            connection.rollback();
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

        String findClientQuery = "SELECT client_id FROM apartments WHERE id = ?";
        String deleteClientQuery = "DELETE FROM clients WHERE id = ?";
        String updateApartmentQuery = "UPDATE apartments SET status = ?, client_id = NULL WHERE id = ?";

        try (Connection connection = databaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            Integer clientId = null;

            try (PreparedStatement findClientStatement = connection.prepareStatement(findClientQuery)) {
                findClientStatement.setInt(1, id);
                try (ResultSet resultSet = findClientStatement.executeQuery()) {
                    if (resultSet.next()) {
                        clientId = resultSet.getInt("client_id");
                    }
                }
            }

            if (clientId != null) {

                try (PreparedStatement deleteClientStatement = connection.prepareStatement(deleteClientQuery)) {
                    deleteClientStatement.setInt(1, clientId);
                    deleteClientStatement.executeUpdate();
                }
            }

            try (PreparedStatement updateApartmentStatement = connection.prepareStatement(updateApartmentQuery)) {
                updateApartmentStatement.setString(1, ReservationStatus.AVAILABLE.name());
                updateApartmentStatement.setInt(2, id);
                updateApartmentStatement.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection connection = databaseConnection.getConnection()) {
                connection.rollback(); // Откатываем изменения в случае ошибки
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public List<Apartment> getPaginatedAndSortedApartments(int page, int size, String sortBy) {
        List<Apartment> apartments = new ArrayList<>();
        String query = """
                    SELECT a.id, a.price, a.status, c.id AS client_id, c.name AS client_name
                    FROM apartments a
                    LEFT JOIN clients c ON a.client_id = c.id
                    ORDER BY %s
                    LIMIT ? OFFSET ?
                """.formatted(sortBy);

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

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
}