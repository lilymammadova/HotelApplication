package org.liliya.hotelapp.service;

import org.liliya.hotelapp.model.Apartment;
import org.liliya.hotelapp.model.Client;
import org.liliya.hotelapp.model.ReservationStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ApartmentServiceImpl implements ApartmentService {
    private final List<Apartment> apartments = new ArrayList<>();
    private int idCounter = 1;

    @Override
    public void register(double price) {
        Apartment apartment = new Apartment(idCounter++, price, ReservationStatus.AVAILABLE, null);
        apartments.add(apartment);
    }

    @Override
    public boolean reserve(Client client) {
        return apartments.stream()
                .filter(apartment -> apartment.getReservationStatus().equals(ReservationStatus.AVAILABLE))
                .findFirst()
                .map(apartment -> {
                    apartment.setReservationStatus(ReservationStatus.RESERVED);
                    apartment.setClient(client);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean release(int id) {
        return apartments.stream().filter(apartment -> apartment.getId() == id)
                .findFirst()
                .map(apartment -> {
                    apartment.setReservationStatus(ReservationStatus.AVAILABLE);
                    apartment.setClient(null);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public List<Apartment> getPaginatedAndSortedApartments(int page, int size, Comparator<Apartment> comparator) {
        return apartments.stream()
                .sorted(comparator)
                .skip((long) (page - 1) * size)
                .limit(size)
                .collect(Collectors.toList());
    }
    public List<Apartment> getAllApartments(){
        return apartments;
    }
}
