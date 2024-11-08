package org.liliya.hotelapp;

import org.liliya.hotelapp.service.ApartmentService;
import org.liliya.hotelapp.service.ApartmentServiceImpl;
import org.liliya.hotelapp.ui.Console;

import java.util.Scanner;

public class HotelApp {
    public static void main(String[] args) {
        ApartmentService apartmentService = new ApartmentServiceImpl();
        Scanner scanner = new Scanner(System.in);
        Console console = new Console(apartmentService, scanner);
        console.start();
    }
}
