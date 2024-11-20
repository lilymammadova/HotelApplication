package org.liliya.hotelapp.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    private static Configuration instance;
    private boolean statusChangeAvailability;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;

    private Configuration() {
        loadProperties();
    }

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    private void loadProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application_config.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
                statusChangeAvailability = Boolean.parseBoolean(properties.getProperty("statusChangeAvailability", "false"));
                dbUrl = properties.getProperty("db.url");
                dbUsername = properties.getProperty("db.username");
                dbPassword = properties.getProperty("db.password");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            statusChangeAvailability = false;
        }
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public boolean statusChangeAvailability() {
        return statusChangeAvailability;
    }
}
