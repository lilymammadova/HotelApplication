package org.liliya.hotelapp.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.liliya.hotelapp.configuration.Configuration;

import java.io.*;

public class StatePersistenceImpl<T> implements StatePersistence<T> {
    private final String filePath;
    private final ObjectMapper objectMapper;


    public StatePersistenceImpl(ObjectMapper objectMapper) {
        Configuration configuration = new Configuration();
        this.filePath = configuration.getFilePath();
        this.objectMapper = objectMapper;
    }

    @Override
    public void saveState(T object) {
        try {
            objectMapper.writeValue(new File(filePath), object);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public <T> T loadState(TypeReference<T> tTypeReference) {
        try {
            return objectMapper.readValue(new File(filePath), tTypeReference);
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}

