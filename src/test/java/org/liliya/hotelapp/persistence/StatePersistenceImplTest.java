package org.liliya.hotelapp.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.liliya.hotelapp.model.Apartment;
import org.liliya.hotelapp.model.ReservationStatus;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatePersistenceImplTest {
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private StatePersistenceImpl<Apartment> statePersistence;

    @Test
    void givenApartmentList_WhenSavingState_ThenObjectsShouldBeAddedToJsonFile() throws IOException {
        Apartment apartment = new Apartment(6, 40, ReservationStatus.AVAILABLE, null);
        statePersistence.saveState(apartment);
        verify(objectMapper, times(1)).writeValue(ArgumentMatchers.any(File.class), ArgumentMatchers.eq(apartment));
    }

    @Test
    void givenApartmentList_WhenLoading_ThenApartmentsShouldBeLoadedFromJsonFile() throws IOException {
        TypeReference<List<Apartment>> typeReference = new TypeReference<>() {
        };
        List<Apartment> expectedApartments = new ArrayList<>();
        when(objectMapper.readValue(any(File.class), ArgumentMatchers.eq(typeReference))).thenReturn(expectedApartments);
        List<Apartment> resultList = statePersistence.loadState(typeReference);
        verify(objectMapper, times(1)).readValue(any(File.class), ArgumentMatchers.eq(typeReference));
        assert resultList == expectedApartments;
    }

}
