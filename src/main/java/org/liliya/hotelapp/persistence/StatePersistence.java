package org.liliya.hotelapp.persistence;

import com.fasterxml.jackson.core.type.TypeReference;

public interface StatePersistence<T> {
    void saveState(T object);

    <T> T loadState(TypeReference<T> tTypeReference);
}
