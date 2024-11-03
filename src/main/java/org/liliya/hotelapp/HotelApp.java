package org.liliya.hotelapp;

import org.liliya.hotelapp.service.ApartmentService;
import org.liliya.hotelapp.service.ApartmentServiceImpl;
import org.liliya.hotelapp.ui.Console;

public class HotelApp {
    public static void main(String[] args) {
        ApartmentService apartmentService=new ApartmentServiceImpl();
        Console console=new Console(apartmentService);
        console.start();
    }
}
