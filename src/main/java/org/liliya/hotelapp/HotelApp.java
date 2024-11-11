package org.liliya.hotelapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.liliya.hotelapp.configuration.Configuration;
import org.liliya.hotelapp.model.Apartment;
import org.liliya.hotelapp.persistence.StatePersistence;
import org.liliya.hotelapp.persistence.StatePersistenceImpl;
import org.liliya.hotelapp.service.ApartmentService;
import org.liliya.hotelapp.service.ApartmentServiceImpl;
import org.liliya.hotelapp.ui.Console;

import java.util.List;
import java.util.Scanner;

public class HotelApp {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        StatePersistence<List<Apartment>> statePersistence = new StatePersistenceImpl<>(objectMapper);
        Configuration configuration = new Configuration();
        ApartmentService apartmentService = new ApartmentServiceImpl(statePersistence, configuration);
        Scanner scanner = new Scanner(System.in);
        Console console = new Console(apartmentService, scanner, statePersistence);
        console.start();
    }
}
