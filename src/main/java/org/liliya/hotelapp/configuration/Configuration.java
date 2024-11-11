package org.liliya.hotelapp.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    private boolean statusChangeAvailability;
    private String filePath;

    public Configuration() {
        loadProperties();
    }

    private void loadProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application_config.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
                statusChangeAvailability = Boolean.parseBoolean(properties.getProperty("statusChangeAvailability", "false"));
                filePath = properties.getProperty("filePath");

            }
        } catch (IOException exception) {
            exception.printStackTrace();
            statusChangeAvailability = false;
        }
    }

    public boolean statusChangeAvailability() {
        return statusChangeAvailability;
    }

    public String getFilePath() {
        return filePath;
    }
}
