package org.liliya.hotelapp.service;

import org.liliya.hotelapp.model.Apartment;
import org.liliya.hotelapp.model.Client;

import java.util.Comparator;
import java.util.List;

public interface ApartmentService {
    void register(double price);

    boolean reserve(Client client);

    boolean release(int id);

    List<Apartment> getPaginatedAndSortedApartments(int page, int size, Comparator<Apartment> comparator);

}
